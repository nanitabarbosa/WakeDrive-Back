package com.wakedrive.backend.company.controller;

import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.company.dto.CompanyDTO;
import com.wakedrive.backend.company.dto.CompanyDeviceDTO;
import com.wakedrive.backend.company.dto.CompanySummaryDTO;
import com.wakedrive.backend.company.service.CompanyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public List<CompanyDTO> getAll() {
        return companyService.getAll();
    }

    @GetMapping("/summary")
    public PageResponseDTO<CompanySummaryDTO> getSummary(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return companyService.getSummary(search, page, size);
    }

    @GetMapping("/{companyId}/devices")
    public PageResponseDTO<CompanyDeviceDTO> getCompanyDevices(
            @PathVariable Long companyId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return companyService.getCompanyDevices(companyId, search, page, size);
    }
}
