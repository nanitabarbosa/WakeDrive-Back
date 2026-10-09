package com.wakedrive.backend.accessrequest.dto;

import com.wakedrive.backend.accessrequest.entity.AccessRequestStatus;
import java.time.LocalDateTime;
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
