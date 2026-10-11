package com.wakedrive.backend.vehicle.dto;

import com.wakedrive.backend.common.entity.RecordStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {

    @NotBlank
    private String plate;

    @NotNull
    private Long brandId;

    @NotBlank
    private String model;

    @NotNull
    private Integer year;

    @NotNull
    private Long vehicleTypeId;

    @NotNull
    private RecordStatus status;
}
