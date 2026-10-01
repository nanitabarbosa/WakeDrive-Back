package com.wakedrive.backend.dto;

import com.wakedrive.backend.entity.RecordStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDTO {

    @NotBlank
    private String fullName;

    @NotBlank
    private String document;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotNull
    private RecordStatus status;
}
