package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.DriverDTO;
import com.wakedrive.backend.dto.DriverRequestDTO;
import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.RecordStatus;
import com.wakedrive.backend.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public PageResponseDTO<DriverDTO> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RecordStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return driverService.getAll(search, status, page, size);
    }

    @GetMapping("/options")
    public List<FilterOptionDTO> getOptions() {
        return driverService.getOptions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverDTO create(@Valid @RequestBody DriverRequestDTO request) {
        return driverService.create(request);
    }

    @PutMapping("/{id}")
    public DriverDTO update(@PathVariable Long id, @Valid @RequestBody DriverRequestDTO request) {
        return driverService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
