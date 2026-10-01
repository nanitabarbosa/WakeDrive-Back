package com.wakedrive.backend.dto;

import com.wakedrive.backend.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private Boolean read;
}
