package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.DriverDTO;
import com.wakedrive.backend.dto.DriverRequestDTO;
import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.Company;
import com.wakedrive.backend.entity.Driver;
import com.wakedrive.backend.entity.RecordStatus;
import com.wakedrive.backend.exception.DuplicateResourceException;
import com.wakedrive.backend.exception.ResourceNotFoundException;
import com.wakedrive.backend.repository.CompanyRepository;
import com.wakedrive.backend.repository.DriverRepository;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.service.DriverService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final CompanyRepository companyRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<DriverDTO> getAll(String search, RecordStatus status, int page, int size) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        Specification<Driver> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("document")), like),
                        cb.like(cb.lower(root.get("email")), like)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Driver> result = driverRepository.findAll(spec, PageRequest.of(page, size));
        return PageResponseDTO.of(result, this::toDTO);
    }

    @Override
    public DriverDTO create(DriverRequestDTO request) {
        if (driverRepository.findByDocument(request.getDocument()).isPresent()) {
            throw new DuplicateResourceException("Driver already exists with document: " + request.getDocument());
        }
        Company company = companyRepository.findById(currentUserProvider.getCurrentCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        Driver driver = new Driver();
        applyRequest(driver, request);
        driver.setCompany(company);
        return toDTO(driverRepository.save(driver));
    }

    @Override
    public DriverDTO update(Long id, DriverRequestDTO request) {
        Driver driver = findEntity(id);
        if (!driver.getDocument().equals(request.getDocument())
                && driverRepository.findByDocument(request.getDocument()).isPresent()) {
            throw new DuplicateResourceException("Driver already exists with document: " + request.getDocument());
        }
        applyRequest(driver, request);
        return toDTO(driverRepository.save(driver));
    }

    @Override
    public void delete(Long id) {
        driverRepository.delete(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilterOptionDTO> getOptions() {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        return driverRepository.findByCompany_Id(companyId).stream()
                .map(driver -> new FilterOptionDTO(driver.getId(), driver.getFullName()))
                .toList();
    }

    private void applyRequest(Driver driver, DriverRequestDTO request) {
        driver.setFullName(request.getFullName());
        driver.setDocument(request.getDocument());
        driver.setEmail(request.getEmail());
        driver.setPhone(request.getPhone());
        driver.setStatus(request.getStatus());
    }

    private Driver findEntity(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + id));
        if (!driver.getCompany().getId().equals(currentUserProvider.getCurrentCompanyId())) {
            throw new ResourceNotFoundException("Driver not found: " + id);
        }
        return driver;
    }

    private DriverDTO toDTO(Driver driver) {
        return DriverDTO.builder()
                .id(driver.getId())
                .fullName(driver.getFullName())
                .document(driver.getDocument())
                .email(driver.getEmail())
                .phone(driver.getPhone())
                .status(driver.getStatus())
                .build();
    }
}
