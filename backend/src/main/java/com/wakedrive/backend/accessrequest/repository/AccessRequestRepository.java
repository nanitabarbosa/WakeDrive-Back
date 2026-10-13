package com.wakedrive.backend.accessrequest.repository;

import com.wakedrive.backend.accessrequest.entity.AccessRequest;
import com.wakedrive.backend.accessrequest.entity.AccessRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    Page<AccessRequest> findByStatus(AccessRequestStatus status, Pageable pageable);

    long countByStatus(AccessRequestStatus status);

    @Query("select count(a) > 0 from AccessRequest a where replace(a.nit, '-', '') = :nit and a.status = :status")
    boolean existsByNormalizedNitAndStatus(@Param("nit") String nit, @Param("status") AccessRequestStatus status);

    boolean existsByAdminEmailIgnoreCaseAndStatus(String adminEmail, AccessRequestStatus status);
}
