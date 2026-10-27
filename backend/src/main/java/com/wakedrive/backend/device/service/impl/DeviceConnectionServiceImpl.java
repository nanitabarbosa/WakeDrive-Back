package com.wakedrive.backend.device.service.impl;

import com.wakedrive.backend.alert.entity.Alert;
import com.wakedrive.backend.alert.entity.AlertLevel;
import com.wakedrive.backend.alert.repository.AlertRepository;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.common.exception.ResourceNotFoundException;
import com.wakedrive.backend.common.util.TextNormalizer;
import com.wakedrive.backend.device.dto.DeviceConnectionRequestDTO;
import com.wakedrive.backend.device.entity.Device;
import com.wakedrive.backend.device.repository.DeviceRepository;
import com.wakedrive.backend.device.service.DeviceConnectionService;
import com.wakedrive.backend.eventtype.entity.EventType;
import com.wakedrive.backend.eventtype.repository.EventTypeRepository;
import com.wakedrive.backend.vinculation.entity.Vinculation;
import com.wakedrive.backend.vinculation.repository.VinculationRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceConnectionServiceImpl implements DeviceConnectionService {

    private static final String DEVICE_OFF = "DEVICE_OFF";
    private static final String CONNECTION_LOST = "CONNECTION_LOST";
    /** Alertas que indican que el conductor estaba en riesgo justo antes de desconectarse. */
    private static final List<String> DRIVER_RISK_EVENTS = List.of("DROWSINESS", "FACE_NOT_DETECTED");
    private static final String UNKNOWN_LOCATION = "Sin ubicación registrada";

    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;
    private final VinculationRepository vinculationRepository;
    private final EventTypeRepository eventTypeRepository;

    @Value("${app.device.api-key}")
    private String apiKey;

    @Value("${app.device.offline-after-seconds:120}")
    private long offlineAfterSeconds;

    @Override
    public void heartbeat(String deviceKey, DeviceConnectionRequestDTO request) {
        validateKey(deviceKey);
        Device device = findBySerial(request.getSerial());
        LocalDateTime now = LocalDateTime.now();
        resolvePendingAlerts(device, now);
        device.setLastConnection(now);
        deviceRepository.save(device);
    }

    @Override
    public void shutdown(String deviceKey, DeviceConnectionRequestDTO request) {
        validateKey(deviceKey);
        Device device = findBySerial(request.getSerial());
        LocalDateTime now = LocalDateTime.now();
        // Si avisa que se apaga es porque está conectado: lo que estuviera en espera ya se puede calcular
        resolvePendingAlerts(device, now);
        device.setLastConnection(now);
        deviceRepository.save(device);
        String location = StringUtils.hasText(request.getLocation()) ? request.getLocation().trim() : lastKnownLocation(device);
        createPendingAlert(device, DEVICE_OFF, now, location);
    }

    @Override
    public void detectLostConnections() {
        LocalDateTime limit = LocalDateTime.now().minusSeconds(offlineAfterSeconds);
        for (Device device : deviceRepository.findByStatusAndLastConnectionBefore(RecordStatus.ACTIVE, limit)) {
            // Ya tiene una alerta en espera (apagado o sin señal): no se repite mientras no vuelva
            if (alertRepository.existsByDevice_IdAndLevel(device.getId(), AlertLevel.PENDING)) {
                continue;
            }
            // Desde su última conexión es que no se sabe nada del dispositivo
            createPendingAlert(device, CONNECTION_LOST, device.getLastConnection(), lastKnownLocation(device));
        }
    }

    private void validateKey(String deviceKey) {
        if (deviceKey == null || !MessageDigest.isEqual(
                deviceKey.getBytes(StandardCharsets.UTF_8), apiKey.getBytes(StandardCharsets.UTF_8))) {
            throw new AccessDeniedException("Llave de dispositivo inválida");
        }
    }

    private Device findBySerial(String serial) {
        return deviceRepository.findBySerial(TextNormalizer.code(serial))
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado"));
    }

    private String lastKnownLocation(Device device) {
        return alertRepository.findFirstByDevice_IdOrderByDetectedAtDesc(device.getId())
                .map(Alert::getLocation)
                .orElse(UNKNOWN_LOCATION);
    }

    /** Sin vinculación no hay conductor ni vehículo a quien asignarle la alerta: no se crea. */
    private void createPendingAlert(Device device, String eventTypeName, LocalDateTime detectedAt, String location) {
        Vinculation vinculation = vinculationRepository.findByDevice_Id(device.getId()).orElse(null);
        if (vinculation == null) {
            return;
        }
        EventType eventType = eventTypeRepository.findByName(eventTypeName)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evento no encontrado: " + eventTypeName));
        alertRepository.save(Alert.builder()
                .detectedAt(detectedAt)
                .driver(vinculation.getDriver())
                .vehicle(vinculation.getVehicle())
                .device(device)
                .location(location)
                .eventType(eventType)
                .durationSeconds(0)
                .level(AlertLevel.PENDING)
                .build());
    }

    /** Cada alerta en espera recibe los segundos que el dispositivo estuvo apagado o sin señal y su nivel definitivo. */
    private void resolvePendingAlerts(Device device, LocalDateTime reconnectedAt) {
        for (Alert alert : alertRepository.findByDevice_IdAndLevel(device.getId(), AlertLevel.PENDING)) {
            long seconds = Math.max(0, Duration.between(alert.getDetectedAt(), reconnectedAt).getSeconds());
            alert.setDurationSeconds((int) Math.min(seconds, Integer.MAX_VALUE));
            alert.setLevel(calculateLevel(alert, seconds));
            alertRepository.save(alert);
        }
    }

    private AlertLevel calculateLevel(Alert alert, long seconds) {
        Integer driverId = alert.getDriver().getId();
        String eventTypeName = alert.getEventType().getName();
        LocalDateTime detectedAt = alert.getDetectedAt();

        // Se desconectó hasta 5 min después de una somnolencia o un rostro no detectado: pudo ser para callar la alarma
        boolean afterDriverRisk = alertRepository.existsByDriver_IdAndEventType_NameInAndDetectedAtBetween(
                driverId, DRIVER_RISK_EVENTS, detectedAt.minusMinutes(5), detectedAt);
        // Cuántas del mismo tipo lleva el conductor en el día, contando esta
        long sameDayCount = alertRepository.countByDriver_IdAndEventType_NameAndDetectedAtBetween(
                driverId, eventTypeName, detectedAt.toLocalDate().atStartOfDay(), detectedAt);

        if (DEVICE_OFF.equals(eventTypeName)) {
            if (afterDriverRisk || sameDayCount >= 3) {
                return AlertLevel.HIGH;
            }
            // Menos de 5 min es un reinicio y más de 4 h es fin de jornada; en medio el vehículo pudo andar sin monitoreo
            if (seconds < 5 * 60 || seconds > 4 * 60 * 60) {
                return AlertLevel.LOW;
            }
            return AlertLevel.MEDIUM;
        }

        if (afterDriverRisk || seconds > 60 * 60) {
            return AlertLevel.HIGH;
        }
        if (seconds >= 15 * 60 || sameDayCount >= 3) {
            return AlertLevel.MEDIUM;
        }
        return AlertLevel.LOW;
    }
}
