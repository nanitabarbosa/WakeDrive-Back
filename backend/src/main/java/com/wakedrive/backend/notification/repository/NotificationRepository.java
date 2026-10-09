package com.wakedrive.backend.notification.repository;

import com.wakedrive.backend.notification.entity.Notification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByCompany_Id(Long companyId, Pageable pageable);

    Page<Notification> findByCompany_IdAndRead(Long companyId, boolean read, Pageable pageable);

    long countByCompany_Id(Long companyId);

    long countByCompany_IdAndReadFalse(Long companyId);

    List<Notification> findByCompany_IdAndReadFalse(Long companyId);
}
