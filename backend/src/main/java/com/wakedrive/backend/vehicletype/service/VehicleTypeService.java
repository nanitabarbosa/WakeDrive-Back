package com.wakedrive.backend.vehicletype.service;

import com.wakedrive.backend.vehicletype.dto.VehicleTypeDTO;
import java.util.List;

public interface VehicleTypeService {

    List<VehicleTypeDTO> getAll();
}
