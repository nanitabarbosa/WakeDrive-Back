package com.wakedrive.backend.vehicle.dto;

import com.wakedrive.backend.brand.dto.BrandDTO;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.vehicletype.dto.VehicleTypeDTO;
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
    private BrandDTO brand;
    private String model;
    private Integer year;
    private VehicleTypeDTO type;
    private RecordStatus status;
}
