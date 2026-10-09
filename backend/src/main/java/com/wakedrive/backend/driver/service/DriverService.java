package com.wakedrive.backend.driver.service;

import com.wakedrive.backend.common.dto.FilterOptionDTO;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.driver.dto.DriverDTO;
import com.wakedrive.backend.driver.dto.DriverRequestDTO;
import java.util.List;

public interface DriverService {

    PageResponseDTO<DriverDTO> getAll(String search, RecordStatus status, int page, int size);

    DriverDTO create(DriverRequestDTO request);

    DriverDTO update(Long id, DriverRequestDTO request);

    void delete(Long id);

    List<FilterOptionDTO> getOptions();
}
