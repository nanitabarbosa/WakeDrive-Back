package com.wakedrive.backend.vehicle.service;

import com.wakedrive.backend.common.dto.FilterOptionDTO;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.vehicle.dto.VehicleDTO;
import com.wakedrive.backend.vehicle.dto.VehicleRequestDTO;
import java.util.List;

public interface VehicleService {

    PageResponseDTO<VehicleDTO> getAll(String search, Integer vehicleTypeId, RecordStatus status, int page, int size);

    VehicleDTO create(VehicleRequestDTO request);

    VehicleDTO update(Integer id, VehicleRequestDTO request);

    void delete(Integer id);

    List<FilterOptionDTO> getOptions();
}
