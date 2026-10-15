package com.wakedrive.backend.device.dto;

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
public class DeviceRequestDTO {

    @NotBlank(message = "Es obligatorio")
    private String serial;

    @NotNull(message = "Es obligatorio")
    private RecordStatus status;
}
