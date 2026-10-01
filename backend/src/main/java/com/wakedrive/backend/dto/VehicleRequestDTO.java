package com.wakedrive.backend.dto;

import com.wakedrive.backend.entity.RecordStatus;
import com.wakedrive.backend.entity.VehicleType;
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

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull
    private Integer year;

    @NotNull
    private VehicleType type;

    @NotNull
    private RecordStatus status;
}
