package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.*;
import com.wakedrive.backend.entity.*;
import com.wakedrive.backend.repository.*;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.service.DashboardService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;
    private final VinculationRepository vinculationRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public DashboardStatsDTO getStats(LocalDate from, LocalDate to) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        LocalDate rangeFrom = from != null ? from : LocalDate.now().minusDays(6);
        LocalDate rangeTo = to != null ? to : LocalDate.now();
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

        long devicesTotal = deviceRepository.countByCompany_Id(companyId);
        long devicesActive = deviceRepository.countByCompany_IdAndStatus(companyId, RecordStatus.ACTIVE);
        long devicesOffline = devicesTotal - devicesActive;

        long alertsCurrent = countAlerts(companyId, fromStart, toEnd);
        long alertsPrevious = countAlerts(companyId, prevFromStart, prevToEnd);

        return DashboardStatsDTO.builder()
                .vehicles(TrendStatDTO.builder().total(vehicleRepository.countByCompany_Id(companyId)).variation(variation(vehiclesCurrent, vehiclesPrevious)).build())
                .activeUsers(TrendStatDTO.builder().total(driverRepository.countByCompany_Id(companyId)).variation(variation(activeUsersCurrent, activeUsersPrevious)).build())
                .devices(DeviceCountsDTO.builder().total(devicesTotal).active(devicesActive).offline(devicesOffline).build())
                .alertsToday(TrendStatDTO.builder().total(alertsCurrent).variation(variation(alertsCurrent, alertsPrevious)).build())
                .build();
    }

    @Override
    public List<DeviceSummaryDTO> getDevices(String search, RecordStatus status, int limit) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        Specification<Device> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                predicates.add(cb.like(cb.lower(root.get("serial")), "%" + search.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return deviceRepository.findAll(spec).stream()
                .limit(limit)
                .map(this::toDeviceSummary)
                .toList();
    }

    @Override
    public List<AlertSummaryDTO> getAlerts(String search, AlertType type, int limit) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
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
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
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
    public List<DailyAlertsDTO> getAlertsByDay(int days) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        LocalDate from = LocalDate.now().minusDays(days - 1L);
        List<Alert> alerts = companyAlerts(companyId, from.atStartOfDay(), LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
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
    public List<UserAlertRankingDTO> getTopUsers(int days, int limit) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        LocalDate from = LocalDate.now().minusDays(days - 1L);
        List<Alert> alerts = companyAlerts(companyId, from.atStartOfDay(), LocalDateTime.of(LocalDate.now(), LocalTime.MAX));

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
                .type(alert.getType())
                .durationSeconds(alert.getDurationSeconds())
                .level(alert.getLevel())
                .build();
    }
}
