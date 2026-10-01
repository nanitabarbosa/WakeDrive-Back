package com.wakedrive.backend.repository;

import com.wakedrive.backend.entity.AccessRequest;
import com.wakedrive.backend.entity.AccessRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    Page<AccessRequest> findByStatus(AccessRequestStatus status, Pageable pageable);

    long countByStatus(AccessRequestStatus status);
}
