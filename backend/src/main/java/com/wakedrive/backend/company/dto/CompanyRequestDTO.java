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
    @Size(max = 30, message = "Máximo 30 caracteres")
    private String nit;

    @NotBlank(message = "Es obligatorio")
    private String name;

    @Size(max = 100, message = "Máximo 100 caracteres")
    private String address;

    @Pattern(regexp = "^\\d{10}$", message = "Debe tener exactamente 10 dígitos")
    private String phone;

    @Email(message = "Ingresa un correo válido")
    private String email;
}
