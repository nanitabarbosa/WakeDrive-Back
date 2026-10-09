package com.wakedrive.backend.alert.dto;

import com.wakedrive.backend.alert.entity.AlertLevel;
import com.wakedrive.backend.alert.entity.AlertType;
import java.time.LocalDateTime;
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
