package com.wakedrive.backend.accessrequest.service.impl;

import com.wakedrive.backend.common.util.PageRequests;
import com.wakedrive.backend.common.util.TextNormalizer;
import com.wakedrive.backend.accessrequest.dto.AccessRequestCountsDTO;
import com.wakedrive.backend.accessrequest.dto.AccessRequestCreateDTO;
import com.wakedrive.backend.accessrequest.dto.AccessRequestDTO;
import com.wakedrive.backend.accessrequest.entity.AccessRequest;
import com.wakedrive.backend.accessrequest.entity.AccessRequestStatus;
import com.wakedrive.backend.accessrequest.repository.AccessRequestRepository;
import com.wakedrive.backend.accessrequest.service.AccessRequestService;
import com.wakedrive.backend.city.entity.City;
import com.wakedrive.backend.city.repository.CityRepository;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.common.exception.DuplicateResourceException;
import com.wakedrive.backend.common.exception.InvalidStateException;
import com.wakedrive.backend.common.exception.ResourceNotFoundException;
import com.wakedrive.backend.common.service.MailService;
import com.wakedrive.backend.company.entity.Company;
import com.wakedrive.backend.company.repository.CompanyRepository;
import com.wakedrive.backend.user.entity.Role;
import com.wakedrive.backend.user.entity.User;
import com.wakedrive.backend.user.repository.RoleRepository;
import com.wakedrive.backend.user.repository.UserRepository;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccessRequestServiceImpl implements AccessRequestService {

    private static final String ADMIN_ROLE = "ADMIN";

    private final AccessRequestRepository accessRequestRepository;
    private final CityRepository cityRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
    private static final int PASSWORD_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void create(AccessRequestCreateDTO request) {
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada"));

        String adminEmail = TextNormalizer.email(request.getAdminEmail());
        String nit = normalizeNit(request.getNit());
        if (companyRepository.existsByNormalizedNit(nit)) {
            throw new DuplicateResourceException("nit", "Ya existe una empresa con el NIT " + request.getNit());
        }
        if (accessRequestRepository.existsByNormalizedNitAndStatus(nit, AccessRequestStatus.PENDING)) {
            throw new DuplicateResourceException("nit", "Ya hay una solicitud pendiente con el NIT " + request.getNit());
        }

        if (userRepository.existsByEmail(adminEmail)) {
            throw new DuplicateResourceException("adminEmail", "Ya existe un usuario con el correo " + adminEmail);
        }
        if (accessRequestRepository.existsByAdminEmailIgnoreCaseAndStatus(adminEmail, AccessRequestStatus.PENDING)) {
            throw new DuplicateResourceException("adminEmail", "Ya hay una solicitud pendiente con el correo " + adminEmail);
        }

        AccessRequest accessRequest = AccessRequest.builder()
                .companyName(request.getCompanyName())
                .nit(TextNormalizer.code(request.getNit()))
                .city(city)
                .address(request.getAddress())
                .companyPhone(request.getCompanyPhone())
                .adminName(request.getAdminName())
                .adminEmail(adminEmail)
                .adminPhone(request.getAdminPhone())
                .status(AccessRequestStatus.PENDING)
                .build();
        accessRequestRepository.save(accessRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AccessRequestDTO> getAll(AccessRequestStatus status, int page, int size) {
        Page<AccessRequest> result = status != null
                ? accessRequestRepository.findByStatus(status, PageRequests.of(page, size))
                : accessRequestRepository.findAll(PageRequests.of(page, size));
        return PageResponseDTO.of(result, this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AccessRequestCountsDTO getCounts() {
        return AccessRequestCountsDTO.builder()
                .total(accessRequestRepository.count())
                .pending(accessRequestRepository.countByStatus(AccessRequestStatus.PENDING))
                .approved(accessRequestRepository.countByStatus(AccessRequestStatus.APPROVED))
                .rejected(accessRequestRepository.countByStatus(AccessRequestStatus.REJECTED))
                .inactive(accessRequestRepository.countByStatus(AccessRequestStatus.INACTIVE))
                .build();
    }

    @Override
    public void approve(Integer id) {
        AccessRequest accessRequest = findPendingEntity(id);

        if (companyRepository.existsByNormalizedNit(normalizeNit(accessRequest.getNit()))) {
            throw new DuplicateResourceException("nit", "Ya existe una empresa con el NIT " + accessRequest.getNit());
        }

        if (userRepository.existsByEmail(TextNormalizer.email(accessRequest.getAdminEmail()))) {
            throw new DuplicateResourceException("adminEmail", "Ya existe un usuario con el correo " + accessRequest.getAdminEmail());
        }

        Company company = Company.builder()
                .nit(TextNormalizer.code(accessRequest.getNit()))
                .name(accessRequest.getCompanyName())
                .address(accessRequest.getAddress())
                .phone(accessRequest.getCompanyPhone())
                .email(TextNormalizer.email(accessRequest.getAdminEmail()))
                .city(accessRequest.getCity())
                .build();
        companyRepository.save(company);

        Role adminRole = roleRepository.findByName(ADMIN_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        String generatedPassword = generatePassword();
        User admin = User.builder()
                .name(accessRequest.getAdminName())
                .email(TextNormalizer.email(accessRequest.getAdminEmail()))
                .password(passwordEncoder.encode(generatedPassword))
                .role(adminRole)
                .company(company)
                .mustChangePassword(true)
                .build();
        userRepository.save(admin);

        mailService.sendAccountCreatedEmail(admin.getEmail(), admin.getName(), admin.getEmail(), generatedPassword);

        accessRequest.setStatus(AccessRequestStatus.APPROVED);
        accessRequestRepository.save(accessRequest);
    }

    @Override
    public void reject(Integer id) {
        AccessRequest accessRequest = findPendingEntity(id);
        accessRequest.setStatus(AccessRequestStatus.REJECTED);
        accessRequestRepository.save(accessRequest);

        mailService.sendCompanyRejectedEmail(
                accessRequest.getAdminEmail(),
                accessRequest.getAdminName(),
                accessRequest.getCompanyName(),
                frontendUrl + "/auth/request-access");
    }

    private String normalizeNit(String nit) {
        return TextNormalizer.code(nit).replace("-", "");
    }

    private AccessRequest findPendingEntity(Integer id) {
        AccessRequest accessRequest = findEntity(id);
        if (accessRequest.getStatus() != AccessRequestStatus.PENDING) {
            throw new InvalidStateException("La solicitud de acceso ya no está pendiente");
        }
        return accessRequest;
    }

    private String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    private AccessRequest findEntity(Integer id) {
        return accessRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de acceso no encontrada"));
    }

    private AccessRequestDTO toDTO(AccessRequest accessRequest) {
        return AccessRequestDTO.builder()
                .id(accessRequest.getId())
                .companyName(accessRequest.getCompanyName())
                .nit(accessRequest.getNit())
                .city(accessRequest.getCity().getName())
                .address(accessRequest.getAddress())
                .companyPhone(accessRequest.getCompanyPhone())
                .adminName(accessRequest.getAdminName())
                .adminEmail(accessRequest.getAdminEmail())
                .adminPhone(accessRequest.getAdminPhone())
                .status(accessRequest.getStatus())
                .createdAt(accessRequest.getCreatedAt())
                .build();
    }
}
