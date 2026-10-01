package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.DeviceDTO;
import com.wakedrive.backend.dto.DeviceRequestDTO;
import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.RecordStatus;

import java.util.List;

public interface DeviceService {

    PageResponseDTO<DeviceDTO> getAll(String search, RecordStatus status, int page, int size);

    DeviceDTO create(DeviceRequestDTO request);

    DeviceDTO update(Long id, DeviceRequestDTO request);

    void delete(Long id);

    List<FilterOptionDTO> getOptions();
}
