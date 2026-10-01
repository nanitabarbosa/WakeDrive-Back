package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.NotificationCountsDTO;
import com.wakedrive.backend.dto.NotificationDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public PageResponseDTO<NotificationDTO> getAll(
            @RequestParam(required = false) Boolean unread,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return notificationService.getAll(unread, page, size);
    }

    @GetMapping("/counts")
    public NotificationCountsDTO getCounts() {
        return notificationService.getCounts();
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @PatchMapping("/read-all")
    public void markAllAsRead() {
        notificationService.markAllAsRead();
    }
}
