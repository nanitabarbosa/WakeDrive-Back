package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.AlertDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.AlertLevel;
import com.wakedrive.backend.entity.AlertType;
import com.wakedrive.backend.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public PageResponseDTO<AlertDTO> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) AlertType type,
            @RequestParam(required = false) AlertLevel level,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return alertService.getAll(from, to, userId, vehicleId, type, level, location, page, size);
    }
}
