package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.NotificationCountsDTO;
import com.wakedrive.backend.dto.NotificationDTO;
import com.wakedrive.backend.dto.PageResponseDTO;

public interface NotificationService {

    PageResponseDTO<NotificationDTO> getAll(Boolean unread, int page, int size);

    NotificationCountsDTO getCounts();

    void markAsRead(Long id);

    void markAllAsRead();
}
