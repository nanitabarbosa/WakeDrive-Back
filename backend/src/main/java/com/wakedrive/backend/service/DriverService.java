package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.DriverDTO;
import com.wakedrive.backend.dto.DriverRequestDTO;
import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.RecordStatus;

import java.util.List;

public interface DriverService {

    PageResponseDTO<DriverDTO> getAll(String search, RecordStatus status, int page, int size);

    DriverDTO create(DriverRequestDTO request);

    DriverDTO update(Long id, DriverRequestDTO request);

    void delete(Long id);

    List<FilterOptionDTO> getOptions();
}
