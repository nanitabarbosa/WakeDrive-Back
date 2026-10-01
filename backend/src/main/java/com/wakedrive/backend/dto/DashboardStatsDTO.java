package com.wakedrive.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private TrendStatDTO vehicles;
    private TrendStatDTO activeUsers;
    private DeviceCountsDTO devices;
    private TrendStatDTO alertsToday;
}
