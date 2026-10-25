package com.wakedrive.backend.alert.dto;

import com.wakedrive.backend.alert.entity.AlertLevel;
import com.wakedrive.backend.eventtype.dto.EventTypeDTO;
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

    private Integer id;
    private LocalDateTime date;
    private Integer userId;
    private String userName;
    private Integer vehicleId;
    private String vehiclePlate;
    private String deviceSerial;
    private String location;
    private EventTypeDTO eventType;
    private Integer durationSeconds;
    private AlertLevel level;
}
