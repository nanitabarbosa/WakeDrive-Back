package com.wakedrive.backend.driver.repository;

import com.wakedrive.backend.driver.entity.Driver;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {

    Optional<Driver> findByDocument(String document);

    List<Driver> findByCompany_IdAndIdNotIn(Long companyId, List<Long> excludedIds);

    List<Driver> findByCompany_Id(Long companyId);

    long countByCompany_Id(Long companyId);

    long countByCompany_IdAndCreatedAtBetween(Long companyId, LocalDateTime from, LocalDateTime to);
}
