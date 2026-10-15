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

    @NotNull(message = "Es obligatorio")
    private Boolean faceRecognitionAlways;

    @NotNull(message = "Es obligatorio")
    private Boolean notifyDeviceShutdown;

    @NotNull(message = "Es obligatorio")
    @Min(value = 1, message = "Debe ser al menos 1")
    @Max(value = 60, message = "No puede ser mayor a 60")
    private Integer alarmDurationSeconds;

    @NotBlank(message = "Es obligatorio")
    @Size(max = 255, message = "Máximo 255 caracteres")
    private String companyName;

    @NotBlank(message = "Es obligatorio")
    @Pattern(regexp = "^\\d{6,12}(-\\d)?$", message = "Debe tener entre 6 y 12 dígitos, con guion y dígito de verificación opcional (ej. 900123456-1)")
    private String nit;

    @NotBlank(message = "Es obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String address;

    @NotBlank(message = "Es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "Debe tener exactamente 10 dígitos")
    private String phone;

    @NotBlank(message = "Es obligatorio")
    @Email(message = "Ingresa un correo válido")
    @Size(max = 255, message = "Máximo 255 caracteres")
    private String email;

    private boolean removeAlarmSound;

    private boolean removeLogo;
}
