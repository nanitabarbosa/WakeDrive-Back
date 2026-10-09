package com.wakedrive.backend.company.service.impl;

import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.common.exception.ResourceNotFoundException;
import com.wakedrive.backend.company.dto.CompanyDTO;
import com.wakedrive.backend.company.dto.CompanyDeviceDTO;
import com.wakedrive.backend.company.dto.CompanySummaryDTO;
import com.wakedrive.backend.company.entity.Company;
import com.wakedrive.backend.company.repository.CompanyRepository;
import com.wakedrive.backend.company.service.CompanyService;
import com.wakedrive.backend.device.entity.Device;
import com.wakedrive.backend.device.repository.DeviceRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final DeviceRepository deviceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAll() {
        return companyRepository.findAll().stream()
                .map(company -> CompanyDTO.builder().id(company.getId()).name(company.getName()).build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<CompanySummaryDTO> getSummary(String search, int page, int size) {
        Specification<Company> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(search)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Company> result = companyRepository.findAll(spec, PageRequest.of(page, size));
        return PageResponseDTO.of(result, company -> CompanySummaryDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .deviceCount(deviceRepository.countByCompany_Id(company.getId()))
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<CompanyDeviceDTO> getCompanyDevices(Long companyId, String search, int page, int size) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found: " + companyId);
        }
        Specification<Device> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                predicates.add(cb.like(cb.lower(root.get("serial")), "%" + search.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Device> result = deviceRepository.findAll(spec, PageRequest.of(page, size));
        return PageResponseDTO.of(result, device -> CompanyDeviceDTO.builder()
                .id(device.getId())
                .serial(device.getSerial())
                .build());
    }
}
