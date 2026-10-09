package com.wakedrive.backend.settings.service;

import com.wakedrive.backend.settings.dto.CompanySettingsDTO;
import com.wakedrive.backend.settings.dto.SettingsRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface SettingsService {

    CompanySettingsDTO getSettings();

    CompanySettingsDTO updateSettings(SettingsRequestDTO request, MultipartFile alarmSound, MultipartFile logo);
}
