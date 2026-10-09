package com.wakedrive.backend.accessrequest.dto;

import jakarta.validation.constraints.Email;
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
public class AccessRequestCreateDTO {

    @NotBlank
    private String companyName;

    @NotBlank
    @Pattern(regexp = "^[\\d-]{5,15}$")
    private String nit;

    @NotNull
    private Long cityId;

    @NotBlank
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\+?[\\d\\s]{7,20}$")
    private String companyPhone;

    @NotBlank
    private String adminName;

    @NotBlank
    @Email
    private String adminEmail;

    @NotBlank
    @Pattern(regexp = "^\\+?[\\d\\s]{7,20}$")
    private String adminPhone;
}
