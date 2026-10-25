package com.wakedrive.backend.vinculation.dto;

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
public class VinculationRequestDTO {

    @NotNull(message = "Es obligatorio")
    private Integer userId;

    @NotNull(message = "Es obligatorio")
    private Integer vehicleId;

    @NotBlank(message = "Es obligatorio")
    private String deviceSerial;
}
