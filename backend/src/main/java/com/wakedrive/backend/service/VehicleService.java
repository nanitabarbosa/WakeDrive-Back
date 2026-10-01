package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.dto.VehicleDTO;
import com.wakedrive.backend.dto.VehicleRequestDTO;
import com.wakedrive.backend.entity.RecordStatus;
import com.wakedrive.backend.entity.VehicleType;

import java.util.List;

public interface VehicleService {

    PageResponseDTO<VehicleDTO> getAll(String search, VehicleType type, RecordStatus status, int page, int size);

    VehicleDTO create(VehicleRequestDTO request);

    VehicleDTO update(Long id, VehicleRequestDTO request);

    void delete(Long id);

    List<FilterOptionDTO> getOptions();
}
