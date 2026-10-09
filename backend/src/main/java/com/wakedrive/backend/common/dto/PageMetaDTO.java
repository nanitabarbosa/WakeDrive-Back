package com.wakedrive.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageMetaDTO {

    private int size;
    private int number;
    private long totalElements;
    private int totalPages;
}
