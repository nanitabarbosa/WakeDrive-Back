package com.wakedrive.backend.settings.service.impl;

import com.wakedrive.backend.common.dto.StoredFileDTO;
import com.wakedrive.backend.common.service.FileStorageService;
import com.wakedrive.backend.company.entity.Company;
import com.wakedrive.backend.company.repository.CompanyRepository;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.settings.dto.CompanySettingsDTO;
import com.wakedrive.backend.settings.dto.SettingsRequestDTO;
import com.wakedrive.backend.settings.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingsServiceImpl implements SettingsService {

    private final CompanyRepository companyRepository;
    private final CurrentUserProvider currentUserProvider;
    private final FileStorageService fileStorageService;

    @Value("${app.uploads.base-url}")
    private String baseUrl;

    @Override
    @Transactional(readOnly = true)
    public CompanySettingsDTO getSettings() {
        return toDTO(currentCompany());
    }

    @Override
    public CompanySettingsDTO updateSettings(SettingsRequestDTO request, MultipartFile alarmSound, MultipartFile logo) {
        Company company = currentCompany();

        company.setFaceRecognitionAlways(request.getFaceRecognitionAlways());
        company.setNotifyDeviceShutdown(request.getNotifyDeviceShutdown());
        company.setAlarmDurationSeconds(request.getAlarmDurationSeconds());
        company.setName(request.getCompanyName());
        company.setNit(request.getNit());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());

        if (request.isRemoveAlarmSound()) {
            fileStorageService.delete(company.getAlarmSoundPath());
            company.setAlarmSoundPath(null);
            company.setAlarmSoundName(null);
            company.setAlarmSoundSize(null);
        }
        if (alarmSound != null && !alarmSound.isEmpty()) {
            fileStorageService.delete(company.getAlarmSoundPath());
            StoredFileDTO stored = fileStorageService.store(alarmSound, "alarm-sounds");
            company.setAlarmSoundPath(relativePath(stored.getUrl()));
            company.setAlarmSoundName(stored.getName());
            company.setAlarmSoundSize(stored.getSize());
        }

        if (request.isRemoveLogo()) {
            fileStorageService.delete(company.getLogoPath());
            company.setLogoPath(null);
            company.setLogoName(null);
            company.setLogoSize(null);
        }
        if (logo != null && !logo.isEmpty()) {
            fileStorageService.delete(company.getLogoPath());
            StoredFileDTO stored = fileStorageService.store(logo, "logos");
            company.setLogoPath(relativePath(stored.getUrl()));
            company.setLogoName(stored.getName());
            company.setLogoSize(stored.getSize());
        }

        return toDTO(companyRepository.save(company));
    }

    private String relativePath(String fullUrl) {
        return fullUrl.substring((baseUrl + "/").length());
    }

    private Company currentCompany() {
        return companyRepository.findById(currentUserProvider.getCurrentCompanyId()).orElseThrow();
    }

    private CompanySettingsDTO toDTO(Company company) {
        StoredFileDTO alarmSound = company.getAlarmSoundPath() != null
                ? StoredFileDTO.builder()
                    .name(company.getAlarmSoundName())
                    .size(company.getAlarmSoundSize() != null ? company.getAlarmSoundSize() : 0)
                    .url(baseUrl + "/" + company.getAlarmSoundPath())
                    .build()
                : null;
        StoredFileDTO logo = company.getLogoPath() != null
                ? StoredFileDTO.builder()
                    .name(company.getLogoName())
                    .size(company.getLogoSize() != null ? company.getLogoSize() : 0)
                    .url(baseUrl + "/" + company.getLogoPath())
                    .build()
                : null;

        return CompanySettingsDTO.builder()
                .faceRecognitionAlways(company.getFaceRecognitionAlways())
                .notifyDeviceShutdown(company.getNotifyDeviceShutdown())
                .alarmDurationSeconds(company.getAlarmDurationSeconds())
                .alarmSound(alarmSound)
                .companyName(company.getName())
                .nit(company.getNit())
                .address(company.getAddress())
                .phone(company.getPhone())
                .logo(logo)
                .build();
    }
}
