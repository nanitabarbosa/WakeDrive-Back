package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.AlertDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.AlertLevel;
import com.wakedrive.backend.entity.AlertType;

import java.time.LocalDate;

public interface AlertService {

    PageResponseDTO<AlertDTO> getAll(LocalDate from, LocalDate to, Long userId, Long vehicleId,
                                      AlertType type, AlertLevel level, String location, int page, int size);
}
