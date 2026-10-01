package com.wakedrive.backend.repository;

import com.wakedrive.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByCompany_Id(Long companyId, Pageable pageable);

    Page<Notification> findByCompany_IdAndRead(Long companyId, boolean read, Pageable pageable);

    long countByCompany_Id(Long companyId);

    long countByCompany_IdAndReadFalse(Long companyId);

    List<Notification> findByCompany_IdAndReadFalse(Long companyId);
}
