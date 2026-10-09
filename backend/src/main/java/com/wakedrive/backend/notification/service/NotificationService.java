package com.wakedrive.backend.notification.service;

import com.wakedrive.backend.common.dto.PageResponseDTO;
import com.wakedrive.backend.notification.dto.NotificationCountsDTO;
import com.wakedrive.backend.notification.dto.NotificationDTO;

public interface NotificationService {

    PageResponseDTO<NotificationDTO> getAll(Boolean unread, int page, int size);

    NotificationCountsDTO getCounts();

    void markAsRead(Long id);

    void markAllAsRead();
}
