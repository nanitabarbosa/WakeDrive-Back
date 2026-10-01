package com.wakedrive.backend.dto;

import com.wakedrive.backend.entity.AccessRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequestDTO {

    private Long id;
    private String companyName;
    private String nit;
    private String country;
    private String city;
    private String address;
    private String companyPhone;
    private String adminName;
    private String adminEmail;
    private String adminPhone;
    private AccessRequestStatus status;
    private LocalDateTime createdAt;
}
