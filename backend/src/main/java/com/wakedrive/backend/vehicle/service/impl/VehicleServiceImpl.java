package com.wakedrive.backend.vehicle.service.impl;

import com.wakedrive.backend.common.util.PageRequests;
import com.wakedrive.backend.common.util.TextNormalizer;
import com.wakedrive.backend.brand.dto.BrandDTO;
import com.wakedrive.backend.brand.entity.Brand;
import com.wakedrive.backend.brand.repository.BrandRepository;
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
import com.wakedrive.backend.vehicle.repository.VehicleRepository;
import com.wakedrive.backend.vehicle.service.VehicleService;
import com.wakedrive.backend.vehicletype.dto.VehicleTypeDTO;
import com.wakedrive.backend.vehicletype.entity.VehicleType;
import com.wakedrive.backend.vehicletype.repository.VehicleTypeRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final BrandRepository brandRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<VehicleDTO> getAll(String search, Integer vehicleTypeId, RecordStatus status, int page, int size) {
        Integer companyId = currentUserProvider.getCurrentCompanyId();
        Specification<Vehicle> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("plate")), like),
                        cb.like(cb.lower(root.get("brand").get("name")), like)
                ));
            }
            if (vehicleTypeId != null) {
                predicates.add(cb.equal(root.get("type").get("id"), vehicleTypeId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Vehicle> result = vehicleRepository.findAll(spec, PageRequests.of(page, size));
        return PageResponseDTO.of(result, this::toDTO);
    }

    @Override
    public VehicleDTO create(VehicleRequestDTO request) {
        String plate = TextNormalizer.code(request.getPlate());
        if (vehicleRepository.findByPlate(plate).isPresent()) {
            throw new DuplicateResourceException("plate", "Ya existe un vehículo con la placa " + plate);
        }
        Company company = companyRepository.findById(currentUserProvider.getCurrentCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
        Vehicle vehicle = new Vehicle();
        applyRequest(vehicle, request);
        vehicle.setCompany(company);
        return toDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleDTO update(Integer id, VehicleRequestDTO request) {
        Vehicle vehicle = findEntity(id);
        String plate = TextNormalizer.code(request.getPlate());
        if (!vehicle.getPlate().equals(plate)
                && vehicleRepository.findByPlate(plate).isPresent()) {
            throw new DuplicateResourceException("plate", "Ya existe un vehículo con la placa " + plate);
        }
        applyRequest(vehicle, request);
        return toDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public void delete(Integer id) {
        vehicleRepository.delete(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilterOptionDTO> getOptions() {
        Integer companyId = currentUserProvider.getCurrentCompanyId();
        return vehicleRepository.findByCompany_Id(companyId).stream()
                .map(vehicle -> new FilterOptionDTO(vehicle.getId(), vehicle.getPlate()))
                .toList();
    }

    private void applyRequest(Vehicle vehicle, VehicleRequestDTO request) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
        VehicleType vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de vehículo no encontrado"));
        vehicle.setPlate(TextNormalizer.code(request.getPlate()));
        vehicle.setBrand(brand);
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setType(vehicleType);
        vehicle.setStatus(request.getStatus());
    }

    private Vehicle findEntity(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));
        if (!vehicle.getCompany().getId().equals(currentUserProvider.getCurrentCompanyId())) {
            throw new ResourceNotFoundException("Vehículo no encontrado");
        }
        return vehicle;
    }

    private VehicleDTO toDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .id(vehicle.getId())
                .plate(vehicle.getPlate())
                .brand(BrandDTO.builder().id(vehicle.getBrand().getId()).name(vehicle.getBrand().getName()).build())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .type(VehicleTypeDTO.builder().id(vehicle.getType().getId()).name(vehicle.getType().getName()).build())
                .status(vehicle.getStatus())
                .build();
    }
}
