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
public class AlertSummaryDTO {

    private Long id;
    private LocalDateTime date;
    private String user;
    private String vehiclePlate;
    private String location;
    private AlertType type;
    private Integer durationSeconds;
    private AlertLevel level;
}
