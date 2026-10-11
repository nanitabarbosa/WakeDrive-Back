package com.wakedrive.backend.vehicletype.service.impl;

import com.wakedrive.backend.vehicletype.dto.VehicleTypeDTO;
import com.wakedrive.backend.vehicletype.entity.VehicleType;
import com.wakedrive.backend.vehicletype.repository.VehicleTypeRepository;
import com.wakedrive.backend.vehicletype.service.VehicleTypeService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleTypeServiceImpl implements VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public List<VehicleTypeDTO> getAll() {
        return vehicleTypeRepository.findAll().stream()
                .sorted(Comparator.comparing(VehicleType::getName))
                .map(vehicleType -> VehicleTypeDTO.builder().id(vehicleType.getId()).name(vehicleType.getName()).build())
                .toList();
    }
}
