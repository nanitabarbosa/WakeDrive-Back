package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.dto.VehicleDTO;
import com.wakedrive.backend.dto.VehicleRequestDTO;
import com.wakedrive.backend.entity.RecordStatus;
import com.wakedrive.backend.entity.VehicleType;
import com.wakedrive.backend.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public PageResponseDTO<VehicleDTO> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) RecordStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return vehicleService.getAll(search, type, status, page, size);
    }

    @GetMapping("/options")
    public List<FilterOptionDTO> getOptions() {
        return vehicleService.getOptions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleDTO create(@Valid @RequestBody VehicleRequestDTO request) {
        return vehicleService.create(request);
    }

    @PutMapping("/{id}")
    public VehicleDTO update(@PathVariable Long id, @Valid @RequestBody VehicleRequestDTO request) {
        return vehicleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
