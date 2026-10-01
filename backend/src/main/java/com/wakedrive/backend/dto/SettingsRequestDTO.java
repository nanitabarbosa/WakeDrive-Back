package com.wakedrive.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsRequestDTO {

    @NotNull
    private Boolean faceRecognitionAlways;

    @NotNull
    private Boolean notifyDeviceShutdown;

    @NotNull
    @Min(1)
    @Max(60)
    private Integer alarmDurationSeconds;

    @NotBlank
    private String companyName;

    @NotBlank
    @Pattern(regexp = "^\\d{6,12}(-\\d)?$")
    private String nit;

    @NotBlank
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\d{7,15}$")
    private String phone;

    private boolean removeAlarmSound;

    private boolean removeLogo;
}
