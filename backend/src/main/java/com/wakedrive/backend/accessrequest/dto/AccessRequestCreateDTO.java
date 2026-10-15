package com.wakedrive.backend.accessrequest.dto;

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
public class AccessRequestCreateDTO {

    @NotBlank(message = "Es obligatorio")
    @Size(max = 255, message = "Máximo 255 caracteres")
    private String companyName;

    @NotBlank(message = "Es obligatorio")
    @Pattern(regexp = "^[\\d-]{5,15}$", message = "Debe tener entre 5 y 15 caracteres, solo dígitos y guiones")
    private String nit;

    @NotNull(message = "Es obligatorio")
    private Long cityId;

    @NotBlank(message = "Es obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String address;

    @NotBlank(message = "Es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "Debe tener exactamente 10 dígitos")
    private String companyPhone;

    @NotBlank(message = "Es obligatorio")
    @Size(max = 255, message = "Máximo 255 caracteres")
    private String adminName;

    @NotBlank(message = "Es obligatorio")
    @Email(message = "Ingresa un correo válido")
    @Size(max = 255, message = "Máximo 255 caracteres")
    private String adminEmail;

    @NotBlank(message = "Es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "Debe tener exactamente 10 dígitos")
    private String adminPhone;
}
