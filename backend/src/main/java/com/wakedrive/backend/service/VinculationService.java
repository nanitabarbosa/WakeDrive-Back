package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.dto.VinculationDTO;
import com.wakedrive.backend.dto.VinculationRequestDTO;

import java.util.List;

public interface VinculationService {

    PageResponseDTO<VinculationDTO> getAll(String search, int page, int size);

    VinculationDTO create(VinculationRequestDTO request);

    void delete(Long id);

    List<FilterOptionDTO> getAvailableDrivers();

    List<FilterOptionDTO> getAvailableVehicles();

    List<FilterOptionDTO> getAvailableDevices();
}
