package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.AlertSummaryDTO;
import com.wakedrive.backend.dto.DailyAlertsDTO;
import com.wakedrive.backend.dto.DashboardStatsDTO;
import com.wakedrive.backend.dto.DeviceSummaryDTO;
import com.wakedrive.backend.dto.UserAlertRankingDTO;
import com.wakedrive.backend.entity.AlertType;
import com.wakedrive.backend.entity.RecordStatus;
import com.wakedrive.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStatsDTO getStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return dashboardService.getStats(from, to);
    }

    @GetMapping("/devices")
    public List<DeviceSummaryDTO> getDevices(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RecordStatus status,
            @RequestParam(defaultValue = "5") int limit) {
        return dashboardService.getDevices(search, status, limit);
    }

    @GetMapping("/alerts")
    public List<AlertSummaryDTO> getAlerts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AlertType type,
            @RequestParam(defaultValue = "5") int limit) {
        return dashboardService.getAlerts(search, type, limit);
    }

    @GetMapping("/alerts/by-day")
    public List<DailyAlertsDTO> getAlertsByDay(@RequestParam(defaultValue = "7") int days) {
        return dashboardService.getAlertsByDay(days);
    }

    @GetMapping("/alerts/top-users")
    public List<UserAlertRankingDTO> getTopUsers(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "5") int limit) {
        return dashboardService.getTopUsers(days, limit);
    }
}
