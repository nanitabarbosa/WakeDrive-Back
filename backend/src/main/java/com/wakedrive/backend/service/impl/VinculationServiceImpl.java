package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.dto.VinculationDTO;
import com.wakedrive.backend.dto.VinculationRequestDTO;
import com.wakedrive.backend.entity.Device;
import com.wakedrive.backend.entity.Driver;
import com.wakedrive.backend.entity.Vehicle;
import com.wakedrive.backend.entity.Vinculation;
import com.wakedrive.backend.exception.DuplicateResourceException;
import com.wakedrive.backend.exception.ResourceNotFoundException;
import com.wakedrive.backend.repository.DeviceRepository;
import com.wakedrive.backend.repository.DriverRepository;
import com.wakedrive.backend.repository.VehicleRepository;
import com.wakedrive.backend.repository.VinculationRepository;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.service.VinculationService;
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
public class VinculationServiceImpl implements VinculationService {

    private final VinculationRepository vinculationRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DeviceRepository deviceRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<VinculationDTO> getAll(String search, int page, int size) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        Specification<Vinculation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("driver").get("company").get("id"), companyId));
            if (StringUtils.hasText(search)) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("driver").get("fullName")), like),
                        cb.like(cb.lower(root.get("vehicle").get("plate")), like),
                        cb.like(cb.lower(root.get("device").get("serial")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Vinculation> result = vinculationRepository.findAll(spec, PageRequest.of(page, size));
        return PageResponseDTO.of(result, this::toDTO);
    }

    @Override
    public VinculationDTO create(VinculationRequestDTO request) {
        Long companyId = currentUserProvider.getCurrentCompanyId();

        Driver driver = driverRepository.findById(request.getUserId())
                .filter(d -> d.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + request.getUserId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .filter(v -> v.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + request.getVehicleId()));
        Device device = deviceRepository.findBySerial(request.getDeviceSerial())
                .filter(d -> d.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with serial: " + request.getDeviceSerial()));

        if (vinculationRepository.findByDevice_Serial(device.getSerial()).isPresent()) {
            throw new DuplicateResourceException("Device already linked: " + device.getSerial());
        }

        Vinculation vinculation = Vinculation.builder()
                .driver(driver)
                .vehicle(vehicle)
                .device(device)
                .build();
        return toDTO(vinculationRepository.save(vinculation));
    }

    @Override
    public void delete(Long id) {
        Vinculation vinculation = vinculationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculation not found: " + id));
        if (!vinculation.getDriver().getCompany().getId().equals(currentUserProvider.getCurrentCompanyId())) {
            throw new ResourceNotFoundException("Vinculation not found: " + id);
        }
        vinculationRepository.delete(vinculation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilterOptionDTO> getAvailableDrivers() {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        List<Long> linkedIds = linkedDriverIds(companyId);
        return driverRepository.findByCompany_IdAndIdNotIn(companyId, linkedIds.isEmpty() ? List.of(-1L) : linkedIds).stream()
                .map(driver -> new FilterOptionDTO(driver.getId(), driver.getFullName()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilterOptionDTO> getAvailableVehicles() {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        List<Long> linkedIds = linkedVehicleIds(companyId);
        return vehicleRepository.findByCompany_IdAndIdNotIn(companyId, linkedIds.isEmpty() ? List.of(-1L) : linkedIds).stream()
                .map(vehicle -> new FilterOptionDTO(vehicle.getId(), vehicle.getPlate()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilterOptionDTO> getAvailableDevices() {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        List<Long> linkedIds = linkedDeviceIds(companyId);
        return deviceRepository.findByCompany_IdAndIdNotIn(companyId, linkedIds.isEmpty() ? List.of(-1L) : linkedIds).stream()
                .map(device -> new FilterOptionDTO(device.getId(), device.getSerial()))
                .toList();
    }

    private List<Long> linkedDriverIds(Long companyId) {
        return companyVinculations(companyId).stream().map(v -> v.getDriver().getId()).toList();
    }

    private List<Long> linkedVehicleIds(Long companyId) {
        return companyVinculations(companyId).stream().map(v -> v.getVehicle().getId()).toList();
    }

    private List<Long> linkedDeviceIds(Long companyId) {
        return companyVinculations(companyId).stream().map(v -> v.getDevice().getId()).toList();
    }

    private List<Vinculation> companyVinculations(Long companyId) {
        Specification<Vinculation> spec = (root, query, cb) ->
                cb.equal(root.get("driver").get("company").get("id"), companyId);
        return vinculationRepository.findAll(spec);
    }

    private VinculationDTO toDTO(Vinculation vinculation) {
        return VinculationDTO.builder()
                .id(vinculation.getId())
                .userId(vinculation.getDriver().getId())
                .userName(vinculation.getDriver().getFullName())
                .vehicleId(vinculation.getVehicle().getId())
                .vehiclePlate(vinculation.getVehicle().getPlate())
                .deviceSerial(vinculation.getDevice().getSerial())
                .linkedAt(vinculation.getLinkedAt())
                .build();
    }
}
