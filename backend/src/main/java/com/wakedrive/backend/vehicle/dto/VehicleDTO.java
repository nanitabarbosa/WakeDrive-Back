package com.wakedrive.backend.vehicle.dto;

import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.vehicle.entity.VehicleType;
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
public class VehicleDTO {

    private Long id;
    private String plate;
    private String brand;
    private String model;
    private Integer year;
    private VehicleType type;
    private RecordStatus status;
}
