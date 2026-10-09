package com.wakedrive.backend.alert.service;

import com.wakedrive.backend.alert.dto.AlertDTO;
import com.wakedrive.backend.alert.entity.AlertLevel;
import com.wakedrive.backend.alert.entity.AlertType;
import com.wakedrive.backend.common.dto.PageResponseDTO;
import java.time.LocalDate;

public interface AlertService {

    PageResponseDTO<AlertDTO> getAll(LocalDate from, LocalDate to, Long userId, Long vehicleId,
                                      AlertType type, AlertLevel level, String location, int page, int size);
}
