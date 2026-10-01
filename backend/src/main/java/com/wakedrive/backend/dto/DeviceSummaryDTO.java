package com.wakedrive.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSummaryDTO {

    private String serial;
    private String vehiclePlate;
    private String assignedUser;
    private String status;
    private LocalDateTime lastConnection;
}
