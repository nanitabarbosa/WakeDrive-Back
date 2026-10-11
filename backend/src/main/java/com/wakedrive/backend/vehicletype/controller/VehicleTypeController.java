package com.wakedrive.backend.vehicletype.controller;

import com.wakedrive.backend.vehicletype.dto.VehicleTypeDTO;
import com.wakedrive.backend.vehicletype.service.VehicleTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicle-types")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    @GetMapping
    public List<VehicleTypeDTO> getAll() {
        return vehicleTypeService.getAll();
    }
}
