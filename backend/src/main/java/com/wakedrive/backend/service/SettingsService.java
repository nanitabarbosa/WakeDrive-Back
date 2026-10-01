package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.CompanySettingsDTO;
import com.wakedrive.backend.dto.SettingsRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface SettingsService {

    CompanySettingsDTO getSettings();

    CompanySettingsDTO updateSettings(SettingsRequestDTO request, MultipartFile alarmSound, MultipartFile logo);
}
