package com.wakedrive.backend.company.service;

import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.company.dto.CompanyDTO;
import com.wakedrive.backend.company.dto.CompanyDeviceDTO;
import com.wakedrive.backend.company.dto.CompanySummaryDTO;
import java.util.List;

public interface CompanyService {

    List<CompanyDTO> getAll();

    PageResponseDTO<CompanySummaryDTO> getSummary(String search, int page, int size);

    PageResponseDTO<CompanyDeviceDTO> getCompanyDevices(Long companyId, String search, int page, int size);
}
