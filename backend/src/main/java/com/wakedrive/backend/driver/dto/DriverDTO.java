package com.wakedrive.backend.driver.dto;

import com.wakedrive.backend.common.entity.RecordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDTO {

    private Long id;
    private String fullName;
    private String document;
    private String email;
    private String phone;
    private RecordStatus status;
}
