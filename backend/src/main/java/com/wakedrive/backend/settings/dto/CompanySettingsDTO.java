package com.wakedrive.backend.settings.dto;

import com.wakedrive.backend.common.dto.StoredFileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySettingsDTO {

    private Boolean faceRecognitionAlways;
    private Boolean notifyDeviceShutdown;
    private Integer alarmDurationSeconds;
    private StoredFileDTO alarmSound;
    private String companyName;
    private String nit;
    private String address;
    private String phone;
    private StoredFileDTO logo;
}
