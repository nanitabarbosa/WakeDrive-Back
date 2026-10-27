package com.wakedrive.backend.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Lo que manda el dispositivo al reportarse ("sigo vivo") o al apagarse. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceConnectionRequestDTO {

    @NotBlank(message = "Es obligatorio")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String serial;

    /** Opcional: dónde estaba el vehículo al apagarse el dispositivo. */
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String location;
}
