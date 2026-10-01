package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.AlertDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.Alert;
import com.wakedrive.backend.entity.AlertLevel;
import com.wakedrive.backend.entity.AlertType;
import com.wakedrive.backend.repository.AlertRepository;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.service.AlertService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public PageResponseDTO<AlertDTO> getAll(LocalDate from, LocalDate to, Long userId, Long vehicleId,
                                             AlertType type, AlertLevel level, String location, int page, int size) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        Specification<Alert> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("driver").get("company").get("id"), companyId));
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("detectedAt"), from.atStartOfDay()));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("detectedAt"), LocalDateTime.of(to, LocalTime.MAX)));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.get("driver").get("id"), userId));
            }
            if (vehicleId != null) {
                predicates.add(cb.equal(root.get("vehicle").get("id"), vehicleId));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (level != null) {
                predicates.add(cb.equal(root.get("level"), level));
            }
            if (StringUtils.hasText(location)) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Alert> result = alertRepository.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "detectedAt")));
        return PageResponseDTO.of(result, this::toDTO);
    }

    private AlertDTO toDTO(Alert alert) {
        return AlertDTO.builder()
                .id(alert.getId())
                .date(alert.getDetectedAt())
                .userId(alert.getDriver().getId())
                .userName(alert.getDriver().getFullName())
                .vehicleId(alert.getVehicle().getId())
                .vehiclePlate(alert.getVehicle().getPlate())
                .deviceSerial(alert.getDevice().getSerial())
                .location(alert.getLocation())
                .type(alert.getType())
                .durationSeconds(alert.getDurationSeconds())
                .level(alert.getLevel())
                .build();
    }
}
