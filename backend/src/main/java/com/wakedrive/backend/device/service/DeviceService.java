package com.wakedrive.backend.device.service;

import com.wakedrive.backend.common.dto.FilterOptionDTO;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.device.dto.DeviceDTO;
import com.wakedrive.backend.device.dto.DeviceRequestDTO;
import java.util.List;

public interface DeviceService {

    PageResponseDTO<DeviceDTO> getAll(String search, RecordStatus status, int page, int size);

    DeviceDTO create(DeviceRequestDTO request);

    DeviceDTO update(Long id, DeviceRequestDTO request);

    void delete(Long id);

    List<FilterOptionDTO> getOptions();
}
