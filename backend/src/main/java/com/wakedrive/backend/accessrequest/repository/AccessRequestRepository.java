package com.wakedrive.backend.accessrequest.repository;

import com.wakedrive.backend.accessrequest.entity.AccessRequest;
import com.wakedrive.backend.accessrequest.entity.AccessRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    Page<AccessRequest> findByStatus(AccessRequestStatus status, Pageable pageable);

    long countByStatus(AccessRequestStatus status);
}
