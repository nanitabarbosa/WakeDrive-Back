package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.AccessRequestCountsDTO;
import com.wakedrive.backend.dto.AccessRequestCreateDTO;
import com.wakedrive.backend.dto.AccessRequestDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.AccessRequestStatus;

public interface AccessRequestService {

    void create(AccessRequestCreateDTO request);

    PageResponseDTO<AccessRequestDTO> getAll(AccessRequestStatus status, int page, int size);

    AccessRequestCountsDTO getCounts();

    void approve(Long id);

    void reject(Long id);
}
