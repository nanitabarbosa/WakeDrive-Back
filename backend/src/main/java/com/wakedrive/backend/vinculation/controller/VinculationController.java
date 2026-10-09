package com.wakedrive.backend.vinculation.controller;

import com.wakedrive.backend.common.dto.FilterOptionDTO;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.vinculation.dto.VinculationDTO;
import com.wakedrive.backend.vinculation.dto.VinculationRequestDTO;
import com.wakedrive.backend.vinculation.service.VinculationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vinculations")
@RequiredArgsConstructor
public class VinculationController {

    private final VinculationService vinculationService;

    @GetMapping
    public PageResponseDTO<VinculationDTO> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return vinculationService.getAll(search, page, size);
    }

    @GetMapping("/available-drivers")
    public List<FilterOptionDTO> getAvailableDrivers() {
        return vinculationService.getAvailableDrivers();
    }

    @GetMapping("/available-vehicles")
    public List<FilterOptionDTO> getAvailableVehicles() {
        return vinculationService.getAvailableVehicles();
    }

    @GetMapping("/available-devices")
    public List<FilterOptionDTO> getAvailableDevices() {
        return vinculationService.getAvailableDevices();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VinculationDTO create(@Valid @RequestBody VinculationRequestDTO request) {
        return vinculationService.create(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vinculationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
