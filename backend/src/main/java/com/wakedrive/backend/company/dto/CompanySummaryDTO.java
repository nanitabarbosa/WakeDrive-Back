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
public class CompanySummaryDTO {

    private Long id;
    private String name;
    private String nit;
    private String city;
    private RecordStatus status;
    private long deviceCount;
}
