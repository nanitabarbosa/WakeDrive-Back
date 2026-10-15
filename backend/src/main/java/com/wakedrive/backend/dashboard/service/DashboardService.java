package com.wakedrive.backend.dashboard.service;

import com.wakedrive.backend.alert.dto.AlertSummaryDTO;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.dashboard.dto.DailyAlertsDTO;
import com.wakedrive.backend.dashboard.dto.DashboardStatsDTO;
import com.wakedrive.backend.dashboard.dto.UserAlertRankingDTO;
import com.wakedrive.backend.device.dto.DeviceSummaryDTO;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    DashboardStatsDTO getStats(LocalDate from, LocalDate to);

    List<DeviceSummaryDTO> getDevices(String search, RecordStatus status, LocalDate from, LocalDate to, int limit);

    List<AlertSummaryDTO> getAlerts(String search, Long eventTypeId, LocalDate from, LocalDate to, int limit);

    List<DailyAlertsDTO> getAlertsByDay(LocalDate from, LocalDate to, int days);

    List<UserAlertRankingDTO> getTopUsers(LocalDate from, LocalDate to, int days, int limit);
}
