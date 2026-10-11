package com.wakedrive.backend.brand.controller;

import com.wakedrive.backend.brand.dto.BrandDTO;
import com.wakedrive.backend.brand.dto.BrandRequestDTO;
import com.wakedrive.backend.brand.service.BrandService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public List<BrandDTO> getAll() {
        return brandService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandDTO create(@Valid @RequestBody BrandRequestDTO request) {
        return brandService.create(request);
    }

    @PutMapping("/{id}")
    public BrandDTO update(@PathVariable Long id, @Valid @RequestBody BrandRequestDTO request) {
        return brandService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
