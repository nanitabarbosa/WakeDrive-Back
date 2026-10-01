package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.CompanyDTO;
import com.wakedrive.backend.dto.CompanyDeviceDTO;
import com.wakedrive.backend.dto.CompanySummaryDTO;
import com.wakedrive.backend.dto.PageResponseDTO;

import java.util.List;

public interface CompanyService {

    List<CompanyDTO> getAll();

    PageResponseDTO<CompanySummaryDTO> getSummary(String search, int page, int size);

    PageResponseDTO<CompanyDeviceDTO> getCompanyDevices(Long companyId, String search, int page, int size);
}
