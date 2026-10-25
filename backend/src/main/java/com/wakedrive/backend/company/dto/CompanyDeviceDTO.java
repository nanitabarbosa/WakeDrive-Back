package com.wakedrive.backend.company.dto;

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
public class CompanyDeviceDTO {

    private Integer id;
    private String serial;
    private RecordStatus status;
}
