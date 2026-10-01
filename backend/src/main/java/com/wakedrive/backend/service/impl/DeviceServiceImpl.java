package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.DeviceDTO;
import com.wakedrive.backend.dto.DeviceRequestDTO;
import com.wakedrive.backend.dto.FilterOptionDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.Company;
import com.wakedrive.backend.entity.Device;
import com.wakedrive.backend.entity.RecordStatus;
import com.wakedrive.backend.exception.DuplicateResourceException;
import com.wakedrive.backend.exception.ResourceNotFoundException;
import com.wakedrive.backend.repository.CompanyRepository;
import com.wakedrive.backend.repository.DeviceRepository;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.service.DeviceService;
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
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final CompanyRepository companyRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<DeviceDTO> getAll(String search, RecordStatus status, int page, int size) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
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
        Page<Device> result = deviceRepository.findAll(spec, PageRequest.of(page, size));
        return PageResponseDTO.of(result, this::toDTO);
    }

    @Override
    public DeviceDTO create(DeviceRequestDTO request) {
        if (deviceRepository.findBySerial(request.getSerial()).isPresent()) {
            throw new DuplicateResourceException("Device already exists with serial: " + request.getSerial());
        }
        Company company = companyRepository.findById(currentUserProvider.getCurrentCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        Device device = new Device();
        device.setSerial(request.getSerial());
        device.setStatus(request.getStatus());
        device.setCompany(company);
        return toDTO(deviceRepository.save(device));
    }

    @Override
    public DeviceDTO update(Long id, DeviceRequestDTO request) {
        Device device = findEntity(id);
        if (!device.getSerial().equals(request.getSerial())
                && deviceRepository.findBySerial(request.getSerial()).isPresent()) {
            throw new DuplicateResourceException("Device already exists with serial: " + request.getSerial());
        }
        device.setSerial(request.getSerial());
        device.setStatus(request.getStatus());
        return toDTO(deviceRepository.save(device));
    }

    @Override
    public void delete(Long id) {
        deviceRepository.delete(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilterOptionDTO> getOptions() {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        return deviceRepository.findByCompany_Id(companyId).stream()
                .map(device -> new FilterOptionDTO(device.getId(), device.getSerial()))
                .toList();
    }

    private Device findEntity(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + id));
        if (!device.getCompany().getId().equals(currentUserProvider.getCurrentCompanyId())) {
            throw new ResourceNotFoundException("Device not found: " + id);
        }
        return device;
    }

    private DeviceDTO toDTO(Device device) {
        return DeviceDTO.builder()
                .id(device.getId())
                .serial(device.getSerial())
                .status(device.getStatus())
                .build();
    }
}
