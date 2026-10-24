package com.wakedrive.backend.driver.dto;

import com.wakedrive.backend.common.entity.RecordStatus;
import jakarta.validation.constraints.Email;
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
public class DriverRequestDTO {

    @NotBlank(message = "Es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String fullName;

    @NotBlank(message = "Es obligatorio")
    @Pattern(regexp = "^\\d{6,10}$", message = "Debe tener entre 6 y 10 dígitos")
    private String document;

    @NotBlank(message = "Es obligatorio")
    @Email(message = "Ingresa un correo válido")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String email;

    @NotBlank(message = "Es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "Debe tener exactamente 10 dígitos")
    private String phone;

    @NotNull(message = "Es obligatorio")
    private RecordStatus status;
}
