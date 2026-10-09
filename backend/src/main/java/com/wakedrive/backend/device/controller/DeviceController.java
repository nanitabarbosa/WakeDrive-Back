package com.wakedrive.backend.device.controller;

import com.wakedrive.backend.common.dto.FilterOptionDTO;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.device.dto.DeviceDTO;
import com.wakedrive.backend.device.dto.DeviceRequestDTO;
import com.wakedrive.backend.device.service.DeviceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
