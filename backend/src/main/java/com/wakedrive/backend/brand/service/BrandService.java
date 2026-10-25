package com.wakedrive.backend.brand.service;

import com.wakedrive.backend.brand.dto.BrandDTO;
import com.wakedrive.backend.brand.dto.BrandRequestDTO;
import java.util.List;

public interface BrandService {

    List<BrandDTO> getAll();

    BrandDTO create(BrandRequestDTO request);

    BrandDTO update(Integer id, BrandRequestDTO request);

    void delete(Integer id);
}
