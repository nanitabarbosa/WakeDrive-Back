package com.wakedrive.backend.vehicle.service.impl;

import com.wakedrive.backend.common.dto.FilterOptionDTO;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.common.exception.DuplicateResourceException;
import com.wakedrive.backend.common.exception.ResourceNotFoundException;
import com.wakedrive.backend.company.entity.Company;
import com.wakedrive.backend.company.repository.CompanyRepository;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.vehicle.dto.VehicleDTO;
import com.wakedrive.backend.vehicle.dto.VehicleRequestDTO;
import com.wakedrive.backend.vehicle.entity.Vehicle;
import com.wakedrive.backend.vehicle.entity.VehicleType;
import com.wakedrive.backend.vehicle.repository.VehicleRepository;
import com.wakedrive.backend.vehicle.service.VehicleService;
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
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CompanyRepository companyRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<VehicleDTO> getAll(String search, VehicleType type, RecordStatus status, int page, int size) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        Specification<Vehicle> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("plate")), like),
                        cb.like(cb.lower(root.get("brand")), like),
                        cb.like(cb.lower(root.get("model")), like)
                ));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Vehicle> result = vehicleRepository.findAll(spec, PageRequest.of(page, size));
        return PageResponseDTO.of(result, this::toDTO);
    }

    @Override
    public VehicleDTO create(VehicleRequestDTO request) {
        if (vehicleRepository.findByPlate(request.getPlate()).isPresent()) {
            throw new DuplicateResourceException("Vehicle already exists with plate: " + request.getPlate());
        }
        Company company = companyRepository.findById(currentUserProvider.getCurrentCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        Vehicle vehicle = new Vehicle();
        applyRequest(vehicle, request);
        vehicle.setCompany(company);
        return toDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleDTO update(Long id, VehicleRequestDTO request) {
        Vehicle vehicle = findEntity(id);
        if (!vehicle.getPlate().equals(request.getPlate())
                && vehicleRepository.findByPlate(request.getPlate()).isPresent()) {
            throw new DuplicateResourceException("Vehicle already exists with plate: " + request.getPlate());
        }
        applyRequest(vehicle, request);
        return toDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public void delete(Long id) {
        vehicleRepository.delete(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilterOptionDTO> getOptions() {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        return vehicleRepository.findByCompany_Id(companyId).stream()
                .map(vehicle -> new FilterOptionDTO(vehicle.getId(), vehicle.getPlate()))
                .toList();
    }

    private void applyRequest(Vehicle vehicle, VehicleRequestDTO request) {
        vehicle.setPlate(request.getPlate());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setType(request.getType());
        vehicle.setStatus(request.getStatus());
    }

    private Vehicle findEntity(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id));
        if (!vehicle.getCompany().getId().equals(currentUserProvider.getCurrentCompanyId())) {
            throw new ResourceNotFoundException("Vehicle not found: " + id);
        }
        return vehicle;
    }

    private VehicleDTO toDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .id(vehicle.getId())
                .plate(vehicle.getPlate())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .type(vehicle.getType())
                .status(vehicle.getStatus())
                .build();
    }
}
