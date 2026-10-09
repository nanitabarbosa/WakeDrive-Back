package com.wakedrive.backend.settings.controller;

import com.wakedrive.backend.settings.dto.CompanySettingsDTO;
import com.wakedrive.backend.settings.dto.SettingsRequestDTO;
import com.wakedrive.backend.settings.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public CompanySettingsDTO getSettings() {
        return settingsService.getSettings();
    }

    @PutMapping(consumes = "multipart/form-data")
    public CompanySettingsDTO updateSettings(
            @Valid @RequestPart("settings") SettingsRequestDTO request,
            @RequestPart(value = "alarmSound", required = false) MultipartFile alarmSound,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        return settingsService.updateSettings(request, alarmSound, logo);
    }
}
