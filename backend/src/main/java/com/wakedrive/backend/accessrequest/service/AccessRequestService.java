package com.wakedrive.backend.accessrequest.service;

import com.wakedrive.backend.accessrequest.dto.AccessRequestCountsDTO;
import com.wakedrive.backend.accessrequest.dto.AccessRequestCreateDTO;
import com.wakedrive.backend.accessrequest.dto.AccessRequestDTO;
import com.wakedrive.backend.accessrequest.entity.AccessRequestStatus;
import com.wakedrive.backend.common.dto.PageResponseDTO;

public interface AccessRequestService {

    void create(AccessRequestCreateDTO request);

    PageResponseDTO<AccessRequestDTO> getAll(AccessRequestStatus status, int page, int size);

    AccessRequestCountsDTO getCounts();

    void approve(Long id);

    void reject(Long id);
}
