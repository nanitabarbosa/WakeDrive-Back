package com.wakedrive.backend.vinculation.service;

import com.wakedrive.backend.common.dto.FilterOptionDTO;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.vinculation.dto.VinculationDTO;
import com.wakedrive.backend.vinculation.dto.VinculationRequestDTO;
import java.util.List;

public interface VinculationService {

    PageResponseDTO<VinculationDTO> getAll(String search, int page, int size);

    VinculationDTO create(VinculationRequestDTO request);

    void delete(Long id);

    List<FilterOptionDTO> getAvailableDrivers();

    List<FilterOptionDTO> getAvailableVehicles();

    List<FilterOptionDTO> getAvailableDevices();
}
