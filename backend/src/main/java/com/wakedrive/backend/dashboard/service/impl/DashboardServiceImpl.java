package com.wakedrive.backend.dashboard.service.impl;

import com.wakedrive.backend.alert.dto.AlertSummaryDTO;
import com.wakedrive.backend.alert.entity.Alert;
import com.wakedrive.backend.alert.repository.AlertRepository;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.eventtype.dto.EventTypeDTO;
import com.wakedrive.backend.dashboard.dto.DailyAlertsDTO;
import com.wakedrive.backend.dashboard.dto.DashboardStatsDTO;
import com.wakedrive.backend.dashboard.dto.TrendStatDTO;
import com.wakedrive.backend.dashboard.dto.UserAlertRankingDTO;
import com.wakedrive.backend.dashboard.service.DashboardService;
import com.wakedrive.backend.device.dto.DeviceCountsDTO;
import com.wakedrive.backend.device.dto.DeviceSummaryDTO;
import com.wakedrive.backend.device.entity.Device;
import com.wakedrive.backend.device.repository.DeviceRepository;
import com.wakedrive.backend.driver.entity.Driver;
import com.wakedrive.backend.driver.repository.DriverRepository;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.vehicle.repository.VehicleRepository;
import com.wakedrive.backend.vinculation.repository.VinculationRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final long MAX_RANGE_DAYS = 366;

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;
    private final VinculationRepository vinculationRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public DashboardStatsDTO getStats(LocalDate from, LocalDate to) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        LocalDate rangeFrom = resolveFrom(from, to, 7);
        LocalDate rangeTo = resolveTo(to);
        long days = ChronoUnit.DAYS.between(rangeFrom, rangeTo) + 1;
        LocalDate previousFrom = rangeFrom.minusDays(days);
        LocalDate previousTo = rangeFrom.minusDays(1);

        LocalDateTime fromStart = rangeFrom.atStartOfDay();
        LocalDateTime toEnd = LocalDateTime.of(rangeTo, LocalTime.MAX);
        LocalDateTime prevFromStart = previousFrom.atStartOfDay();
        LocalDateTime prevToEnd = LocalDateTime.of(previousTo, LocalTime.MAX);

        long vehiclesCurrent = vehicleRepository.countByCompany_IdAndCreatedAtBetween(companyId, fromStart, toEnd);
        long vehiclesPrevious = vehicleRepository.countByCompany_IdAndCreatedAtBetween(companyId, prevFromStart, prevToEnd);

        long activeUsersCurrent = driverRepository.countByCompany_IdAndCreatedAtBetween(companyId, fromStart, toEnd);
        long activeUsersPrevious = driverRepository.countByCompany_IdAndCreatedAtBetween(companyId, prevFromStart, prevToEnd);

        long devicesTotal = deviceRepository.count(devicesInRange(companyId, null, null, fromStart, toEnd));
        long devicesActive = deviceRepository.count(devicesInRange(companyId, null, RecordStatus.ACTIVE, fromStart, toEnd));
        long devicesOffline = devicesTotal - devicesActive;

        long alertsCurrent = countAlerts(companyId, fromStart, toEnd);
        long alertsPrevious = countAlerts(companyId, prevFromStart, prevToEnd);

        return DashboardStatsDTO.builder()
                .vehicles(TrendStatDTO.builder().total(vehiclesCurrent).variation(variation(vehiclesCurrent, vehiclesPrevious)).build())
                .activeUsers(TrendStatDTO.builder().total(activeUsersCurrent).variation(variation(activeUsersCurrent, activeUsersPrevious)).build())
                .devices(DeviceCountsDTO.builder().total(devicesTotal).active(devicesActive).offline(devicesOffline).build())
                .alertsToday(TrendStatDTO.builder().total(alertsCurrent).variation(variation(alertsCurrent, alertsPrevious)).build())
                .build();
    }

    @Override
    public List<DeviceSummaryDTO> getDevices(String search, RecordStatus status, LocalDate from, LocalDate to, int limit) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        validateRange(from, to);
        Specification<Device> spec = devicesInRange(companyId, search, status,
                from != null ? from.atStartOfDay() : null,
                to != null ? LocalDateTime.of(to, LocalTime.MAX) : null);
        return deviceRepository.findAll(spec).stream()
                .limit(limit)
                .map(this::toDeviceSummary)
                .toList();
    }

    @Override
    public List<AlertSummaryDTO> getAlerts(String search, Long eventTypeId, LocalDate from, LocalDate to, int limit) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        validateRange(from, to);
        Specification<Alert> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("driver").get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("location")), like),
                        cb.like(cb.lower(root.get("vehicle").get("plate")), like)
                ));
            }
            if (eventTypeId != null) {
                predicates.add(cb.equal(root.get("eventType").get("id"), eventTypeId));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("detectedAt"), from.atStartOfDay()));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("detectedAt"), LocalDateTime.of(to, LocalTime.MAX)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return alertRepository.findAll(spec).stream()
                .sorted(Comparator.comparing(Alert::getDetectedAt).reversed())
                .limit(limit)
                .map(this::toAlertSummary)
                .toList();
    }

    @Override
    public List<DailyAlertsDTO> getAlertsByDay(LocalDate fromParam, LocalDate toParam, int daysParam) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        LocalDate from = resolveFrom(fromParam, toParam, daysParam);
        LocalDate to = resolveTo(toParam);
        long days = ChronoUnit.DAYS.between(from, to) + 1;
        List<Alert> alerts = companyAlerts(companyId, from.atStartOfDay(), LocalDateTime.of(to, LocalTime.MAX));
        Map<LocalDate, Long> grouped = alerts.stream()
                .collect(Collectors.groupingBy(a -> a.getDetectedAt().toLocalDate(), Collectors.counting()));

        List<DailyAlertsDTO> result = new ArrayList<>();
        for (long i = 0; i < days; i++) {
            LocalDate date = from.plusDays(i);
            result.add(DailyAlertsDTO.builder().date(date).total(grouped.getOrDefault(date, 0L)).build());
        }
        return result;
    }

    @Override
    public List<UserAlertRankingDTO> getTopUsers(LocalDate fromParam, LocalDate toParam, int days, int limit) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        LocalDate from = resolveFrom(fromParam, toParam, days);
        LocalDate to = resolveTo(toParam);
        List<Alert> alerts = companyAlerts(companyId, from.atStartOfDay(), LocalDateTime.of(to, LocalTime.MAX));

        Map<Driver, Long> grouped = alerts.stream()
                .collect(Collectors.groupingBy(Alert::getDriver, Collectors.counting()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<Driver, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> UserAlertRankingDTO.builder()
                        .userId(entry.getKey().getId())
                        .name(entry.getKey().getFullName())
                        .total(entry.getValue())
                        .build())
                .toList();
    }

    /** Fecha final del rango: la recibida, o hoy. */
    private LocalDate resolveTo(LocalDate to) {
        return to != null ? to : LocalDate.now();
    }

    /** Fecha inicial del rango: la recibida, o los últimos {@code defaultDays} días hasta la fecha final. */
    private LocalDate resolveFrom(LocalDate from, LocalDate to, int defaultDays) {
        LocalDate rangeTo = resolveTo(to);
        LocalDate rangeFrom = from != null ? from : rangeTo.minusDays(Math.max(defaultDays, 1) - 1L);
        validateRange(rangeFrom, rangeTo);
        return rangeFrom;
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
            }
            if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
                throw new IllegalArgumentException("El rango de fechas no puede superar " + MAX_RANGE_DAYS + " días");
            }
        }
    }

    /** Dispositivos de la empresa con última conexión dentro del rango (los límites nulos no filtran). */
    private Specification<Device> devicesInRange(Long companyId, String search, RecordStatus status,
                                                 LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                predicates.add(cb.like(cb.lower(root.get("serial")), "%" + search.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastConnection"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastConnection"), to));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private long countAlerts(Long companyId, LocalDateTime from, LocalDateTime to) {
        return companyAlerts(companyId, from, to).size();
    }

    private List<Alert> companyAlerts(Long companyId, LocalDateTime from, LocalDateTime to) {
        Specification<Alert> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("driver").get("company").get("id"), companyId),
                cb.greaterThanOrEqualTo(root.get("detectedAt"), from),
                cb.lessThanOrEqualTo(root.get("detectedAt"), to)
        );
        return alertRepository.findAll(spec);
    }

    private double variation(long current, long previous) {
        if (previous == 0) {
            return current == 0 ? 0 : 100;
        }
        return ((double) (current - previous) / previous) * 100;
    }

    private DeviceSummaryDTO toDeviceSummary(Device device) {
        return vinculationRepository.findByDevice_Serial(device.getSerial())
                .map(v -> DeviceSummaryDTO.builder()
                        .serial(device.getSerial())
                        .vehiclePlate(v.getVehicle().getPlate())
                        .assignedUser(v.getDriver().getFullName())
                        .status(device.getStatus() == RecordStatus.ACTIVE ? "ACTIVE" : "OFFLINE")
                        .lastConnection(device.getLastConnection())
                        .build())
                .orElseGet(() -> DeviceSummaryDTO.builder()
                        .serial(device.getSerial())
                        .vehiclePlate(null)
                        .assignedUser(null)
                        .status(device.getStatus() == RecordStatus.ACTIVE ? "ACTIVE" : "OFFLINE")
                        .lastConnection(device.getLastConnection())
                        .build());
    }

    private AlertSummaryDTO toAlertSummary(Alert alert) {
        return AlertSummaryDTO.builder()
                .id(alert.getId())
                .date(alert.getDetectedAt())
                .user(alert.getDriver().getFullName())
                .vehiclePlate(alert.getVehicle().getPlate())
                .location(alert.getLocation())
                .eventType(EventTypeDTO.builder()
                        .id(alert.getEventType().getId())
                        .name(alert.getEventType().getName())
                        .build())
                .durationSeconds(alert.getDurationSeconds())
                .level(alert.getLevel())
                .build();
    }
}
