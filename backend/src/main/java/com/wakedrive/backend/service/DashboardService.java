package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.AlertSummaryDTO;
import com.wakedrive.backend.dto.DailyAlertsDTO;
import com.wakedrive.backend.dto.DashboardStatsDTO;
import com.wakedrive.backend.dto.DeviceSummaryDTO;
import com.wakedrive.backend.dto.UserAlertRankingDTO;
import com.wakedrive.backend.entity.AlertType;
import com.wakedrive.backend.entity.RecordStatus;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    DashboardStatsDTO getStats(LocalDate from, LocalDate to);

    List<DeviceSummaryDTO> getDevices(String search, RecordStatus status, int limit);

    List<AlertSummaryDTO> getAlerts(String search, AlertType type, int limit);

    List<DailyAlertsDTO> getAlertsByDay(int days);

    List<UserAlertRankingDTO> getTopUsers(int days, int limit);
}
