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

    @NotBlank
    @Size(max = 255)
    private String companyName;

    @NotBlank
    @Pattern(regexp = "^[\\d-]{5,15}$")
    private String nit;

    @NotNull
    private Long cityId;

    @NotBlank
    @Size(max = 150)
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener exactamente 10 dígitos")
    private String companyPhone;

    @NotBlank
    @Size(max = 255)
    private String adminName;

    @NotBlank
    @Email
    @Size(max = 255)
    private String adminEmail;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener exactamente 10 dígitos")
    private String adminPhone;
}
