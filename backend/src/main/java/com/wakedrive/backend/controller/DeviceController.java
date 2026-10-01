package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.DeviceDTO;
import com.wakedrive.backend.dto.DeviceRequestDTO;
import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.RecordStatus;
import com.wakedrive.backend.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public PageResponseDTO<DeviceDTO> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RecordStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return deviceService.getAll(search, status, page, size);
    }

    @GetMapping("/options")
    public List<FilterOptionDTO> getOptions() {
        return deviceService.getOptions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceDTO create(@Valid @RequestBody DeviceRequestDTO request) {
        return deviceService.create(request);
    }

    @PutMapping("/{id}")
    public DeviceDTO update(@PathVariable Long id, @Valid @RequestBody DeviceRequestDTO request) {
        return deviceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
