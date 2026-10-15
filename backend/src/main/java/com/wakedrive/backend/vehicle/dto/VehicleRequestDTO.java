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

    @NotBlank(message = "Es obligatorio")
    private String plate;

    @NotNull(message = "Es obligatorio")
    private Long brandId;

    @NotBlank(message = "Es obligatorio")
    private String model;

    @NotNull(message = "Es obligatorio")
    private Integer year;

    @NotNull(message = "Es obligatorio")
    private Long vehicleTypeId;

    @NotNull(message = "Es obligatorio")
    private RecordStatus status;
}
