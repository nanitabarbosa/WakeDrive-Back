package com.wakedrive.backend.vinculation.dto;

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
public class VinculationDTO {

    private Long id;
    private Long userId;
    private String userName;
    private Long vehicleId;
    private String vehiclePlate;
    private String deviceSerial;
    private LocalDateTime linkedAt;
}
