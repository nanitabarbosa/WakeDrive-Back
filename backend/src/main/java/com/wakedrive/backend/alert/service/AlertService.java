package com.wakedrive.backend.alert.service;

import com.wakedrive.backend.alert.dto.AlertDTO;
import com.wakedrive.backend.alert.entity.AlertLevel;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import java.time.LocalDate;

public interface AlertService {

    PageResponseDTO<AlertDTO> getAll(LocalDate from, LocalDate to, Integer userId, Integer vehicleId,
                                      Integer eventTypeId, AlertLevel level, int page, int size);
}
