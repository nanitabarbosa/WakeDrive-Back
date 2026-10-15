package com.wakedrive.backend.brand.service.impl;

import com.wakedrive.backend.brand.dto.BrandDTO;
import com.wakedrive.backend.brand.dto.BrandRequestDTO;
import com.wakedrive.backend.brand.entity.Brand;
import com.wakedrive.backend.brand.repository.BrandRepository;
import com.wakedrive.backend.brand.service.BrandService;
import com.wakedrive.backend.common.exception.DuplicateResourceException;
import com.wakedrive.backend.common.exception.ResourceNotFoundException;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public List<BrandDTO> getAll() {
        return brandRepository.findAll().stream()
                .sorted(Comparator.comparing(Brand::getName))
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public BrandDTO create(BrandRequestDTO request) {
        if (brandRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("name", "Ya existe una marca con el nombre " + request.getName());
        }
        Brand brand = Brand.builder().name(request.getName()).build();
        return toDTO(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandDTO update(Long id, BrandRequestDTO request) {
        Brand brand = findEntity(id);
        brandRepository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("name", "Ya existe una marca con el nombre " + request.getName());
                });
        brand.setName(request.getName());
        return toDTO(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        brandRepository.delete(findEntity(id));
    }

    private Brand findEntity(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
    }

    private BrandDTO toDTO(Brand brand) {
        return BrandDTO.builder().id(brand.getId()).name(brand.getName()).build();
    }
}
