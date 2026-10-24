package com.wakedrive.backend.vehicle.dto;

import com.wakedrive.backend.common.entity.RecordStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Pattern(regexp = "^[A-Za-z]{3}\\d{2}[0-9A-Za-z]$", message = "Debe tener 6 caracteres sin guion: 3 letras, 2 números y un número o letra (ej. ABC123 o ABC12D)")
    private String plate;

    @NotNull(message = "Es obligatorio")
    private Long brandId;

    @NotBlank(message = "Es obligatorio")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String model;

    @NotNull(message = "Es obligatorio")
    private Integer year;

    @NotNull(message = "Es obligatorio")
    private Long vehicleTypeId;

    @NotNull(message = "Es obligatorio")
    private RecordStatus status;
}
