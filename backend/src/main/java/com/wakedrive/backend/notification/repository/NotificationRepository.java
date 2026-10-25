package com.wakedrive.backend.notification.repository;

import com.wakedrive.backend.notification.entity.Notification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByCompany_Id(Integer companyId, Pageable pageable);

    Page<Notification> findByCompany_IdAndRead(Integer companyId, boolean read, Pageable pageable);

    long countByCompany_Id(Integer companyId);

    long countByCompany_IdAndReadFalse(Integer companyId);

    List<Notification> findByCompany_IdAndReadFalse(Integer companyId);
}
