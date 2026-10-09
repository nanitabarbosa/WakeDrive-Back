package com.wakedrive.backend.accessrequest.dto;

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
public class AccessRequestCountsDTO {

    private long total;
    private long pending;
    private long approved;
    private long rejected;
    private long inactive;
}
