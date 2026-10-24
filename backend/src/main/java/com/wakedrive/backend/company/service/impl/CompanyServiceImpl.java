package com.wakedrive.backend.company.service.impl;

import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.common.exception.ResourceNotFoundException;
import com.wakedrive.backend.common.util.PageRequests;
import com.wakedrive.backend.common.util.TextNormalizer;
import com.wakedrive.backend.company.dto.CompanyDTO;
import com.wakedrive.backend.company.dto.CompanyDeviceCountsDTO;
import com.wakedrive.backend.company.dto.CompanyDeviceDTO;
import com.wakedrive.backend.company.dto.CompanyStatusRequestDTO;
import com.wakedrive.backend.company.dto.CompanySummaryDTO;
import com.wakedrive.backend.company.entity.Company;
import com.wakedrive.backend.company.repository.CompanyRepository;
import com.wakedrive.backend.company.service.CompanyService;
import com.wakedrive.backend.device.entity.Device;
import com.wakedrive.backend.device.repository.DeviceRepository;
import com.wakedrive.backend.device.repository.DeviceRepository.CompanyDeviceTotal;
import com.wakedrive.backend.security.CurrentUserProvider;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
    private final CurrentUserProvider currentUserProvider;

    /** Por nombre y, si dos empresas se llaman igual, por id: sin un orden completo las páginas pueden repetir o saltarse filas. */
    private static final Sort SUMMARY_SORT = Sort.by("name").and(Sort.by("id"));

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAll() {
        List<Company> companies = currentUserProvider.isSuperAdmin()
                ? companyRepository.findAll()
                : companyRepository.findById(currentUserProvider.getCurrentCompanyId()).stream().toList();
        return companies.stream()
                .map(company -> CompanyDTO.builder().id(company.getId()).name(company.getName()).build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<CompanySummaryDTO> getSummary(String search, int page, int size) {
        Specification<Company> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(search)) {
                Predicate byName = cb.like(cb.lower(root.get("name")), "%" + search.trim().toLowerCase() + "%");
                // El NIT se guarda con guion; se compara sin él para que "900123456" y "900123456-1" lo encuentren.
                String nitTerm = TextNormalizer.code(search).replace("-", "");
                if (nitTerm.isEmpty()) {
                    predicates.add(byName);
                } else {
                    Predicate byNit = cb.like(
                            cb.function("replace", String.class, root.get("nit"), cb.literal("-"), cb.literal("")),
                            "%" + nitTerm + "%");
                    predicates.add(cb.or(byName, byNit));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Company> result = companyRepository.findAll(spec, PageRequests.of(page, size, SUMMARY_SORT));
        Map<Long, Long> deviceCounts = countDevices(result.getContent());
        return PageResponseDTO.of(result, company -> toSummary(company, deviceCounts.getOrDefault(company.getId(), 0L)));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<CompanyDeviceDTO> getCompanyDevices(Long companyId, String search, RecordStatus status, int page, int size) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Empresa no encontrada");
        }
        Specification<Device> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                predicates.add(cb.like(cb.lower(root.get("serial")), "%" + search.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Device> result = deviceRepository.findAll(spec, PageRequests.of(page, size));
        return PageResponseDTO.of(result, device -> CompanyDeviceDTO.builder()
                .id(device.getId())
                .serial(device.getSerial())
                .status(device.getStatus())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDeviceCountsDTO getCompanyDeviceCounts(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Empresa no encontrada");
        }
        long active = deviceRepository.countByCompany_IdAndStatus(companyId, RecordStatus.ACTIVE);
        long inactive = deviceRepository.countByCompany_IdAndStatus(companyId, RecordStatus.INACTIVE);
        return CompanyDeviceCountsDTO.builder().total(active + inactive).active(active).inactive(inactive).build();
    }

    @Override
    public CompanySummaryDTO updateStatus(Long companyId, CompanyStatusRequestDTO request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
        company.setStatus(request.getStatus());
        companyRepository.save(company);
        return toSummary(company, deviceRepository.countByCompany_Id(companyId));
    }

    /** Dispositivos por empresa para toda la página en una sola consulta, en vez de una consulta por empresa. */
    private Map<Long, Long> countDevices(List<Company> companies) {
        if (companies.isEmpty()) {
            return Map.of();
        }
        List<Long> companyIds = companies.stream().map(Company::getId).toList();
        return deviceRepository.countByCompanyIds(companyIds).stream()
                .collect(Collectors.toMap(CompanyDeviceTotal::getCompanyId, CompanyDeviceTotal::getTotal));
    }

    private CompanySummaryDTO toSummary(Company company, long deviceCount) {
        return CompanySummaryDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .nit(company.getNit())
                .city(company.getCity() != null ? company.getCity().getName() : null)
                .status(company.getStatus())
                .deviceCount(deviceCount)
                .build();
    }
}
