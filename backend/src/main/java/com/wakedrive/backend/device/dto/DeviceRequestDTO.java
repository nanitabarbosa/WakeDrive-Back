package com.wakedrive.backend.device.dto;

import com.wakedrive.backend.common.entity.RecordStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRequestDTO {

    @NotBlank(message = "Es obligatorio")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String serial;

    @NotNull(message = "Es obligatorio")
    private RecordStatus status;
}
