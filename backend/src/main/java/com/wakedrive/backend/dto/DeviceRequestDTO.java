package com.wakedrive.backend.dto;

import com.wakedrive.backend.entity.RecordStatus;
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

    @NotBlank
    private String serial;

    @NotNull
    private RecordStatus status;
}
