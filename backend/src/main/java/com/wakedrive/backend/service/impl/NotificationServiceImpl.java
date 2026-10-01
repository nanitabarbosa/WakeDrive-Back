package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.NotificationCountsDTO;
import com.wakedrive.backend.dto.NotificationDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.Notification;
import com.wakedrive.backend.exception.ResourceNotFoundException;
import com.wakedrive.backend.repository.NotificationRepository;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<NotificationDTO> getAll(Boolean unread, int page, int size) {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> result = Boolean.TRUE.equals(unread)
                ? notificationRepository.findByCompany_IdAndRead(companyId, false, pageRequest)
                : notificationRepository.findByCompany_Id(companyId, pageRequest);
        return PageResponseDTO.of(result, this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationCountsDTO getCounts() {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        return NotificationCountsDTO.builder()
                .total(notificationRepository.countByCompany_Id(companyId))
                .unread(notificationRepository.countByCompany_IdAndReadFalse(companyId))
                .build();
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        if (!notification.getCompany().getId().equals(currentUserProvider.getCurrentCompanyId())) {
            throw new ResourceNotFoundException("Notification not found: " + id);
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        Long companyId = currentUserProvider.getCurrentCompanyId();
        notificationRepository.findByCompany_IdAndReadFalse(companyId)
                .forEach(notification -> notification.setRead(true));
    }

    private NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .read(notification.getRead())
                .build();
    }
}
