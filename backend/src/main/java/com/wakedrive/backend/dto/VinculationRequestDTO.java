package com.wakedrive.backend.dto;

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

    @NotNull
    private Long userId;

    @NotNull
    private Long vehicleId;

    @NotBlank
    private String deviceSerial;
}
