package com.wakedrive.backend.settings.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Size(max = 255)
    private String companyName;

    @NotBlank
    @Pattern(regexp = "^\\d{6,12}(-\\d)?$")
    private String nit;

    @NotBlank
    @Size(max = 150)
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener exactamente 10 dígitos")
    private String phone;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    private boolean removeAlarmSound;

    private boolean removeLogo;
}
