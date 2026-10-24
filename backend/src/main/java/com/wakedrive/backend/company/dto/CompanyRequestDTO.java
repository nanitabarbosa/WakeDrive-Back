package com.wakedrive.backend.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CompanyRequestDTO {

    @NotBlank(message = "Es obligatorio")
    @Pattern(regexp = "^\\d{9,10}-\\d$", message = "Debe tener el formato 900123456-1 (dígitos, guion y dígito de verificación)")
    private String nit;

    @NotBlank(message = "Es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String name;

    @Size(max = 150, message = "Máximo 150 caracteres")
    private String address;

    @Pattern(regexp = "^\\d{10}$", message = "Debe tener exactamente 10 dígitos")
    private String phone;

    @Email(message = "Ingresa un correo válido")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String email;
}
