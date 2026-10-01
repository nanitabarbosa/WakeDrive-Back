package com.wakedrive.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Size(max = 30)
    private String nit;

    @NotBlank
    private String name;

    @Size(max = 100)
    private String address;

    @Size(max = 20)
    private String phone;

    @Email
    private String email;
}
