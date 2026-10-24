package com.wakedrive.backend.brand.dto;

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
public class BrandRequestDTO {

    @NotBlank(message = "Es obligatorio")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String name;
}
