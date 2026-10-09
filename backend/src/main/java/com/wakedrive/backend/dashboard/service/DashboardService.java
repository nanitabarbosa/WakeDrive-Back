package com.wakedrive.backend.dashboard.service;

import com.wakedrive.backend.alert.dto.AlertSummaryDTO;
import com.wakedrive.backend.alert.entity.AlertType;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.dashboard.dto.DailyAlertsDTO;
import com.wakedrive.backend.dashboard.dto.DashboardStatsDTO;
import com.wakedrive.backend.dashboard.dto.UserAlertRankingDTO;
import com.wakedrive.backend.device.dto.DeviceSummaryDTO;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    DashboardStatsDTO getStats(LocalDate from, LocalDate to);

    List<DeviceSummaryDTO> getDevices(String search, RecordStatus status, int limit);

    List<AlertSummaryDTO> getAlerts(String search, AlertType type, int limit);

    List<DailyAlertsDTO> getAlertsByDay(int days);

    List<UserAlertRankingDTO> getTopUsers(int days, int limit);
}
