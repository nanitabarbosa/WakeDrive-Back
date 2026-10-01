package com.wakedrive.backend.dto;

import com.wakedrive.backend.entity.AlertLevel;
import com.wakedrive.backend.entity.AlertType;
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
public class AlertDTO {

    private Long id;
    private LocalDateTime date;
    private Long userId;
    private String userName;
    private Long vehicleId;
    private String vehiclePlate;
    private String deviceSerial;
    private String location;
    private AlertType type;
    private Integer durationSeconds;
    private AlertLevel level;
}
