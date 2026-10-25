package com.wakedrive.backend.driver.repository;

import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.driver.entity.Driver;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DriverRepository extends JpaRepository<Driver, Integer>, JpaSpecificationExecutor<Driver> {

    Optional<Driver> findByDocument(String document);

    List<Driver> findByCompany_IdAndIdNotIn(Integer companyId, List<Integer> excludedIds);

    List<Driver> findByCompany_IdAndStatusAndIdNotIn(Integer companyId, RecordStatus status, List<Integer> excludedIds);

    List<Driver> findByCompany_Id(Integer companyId);

    long countByCompany_Id(Integer companyId);

    long countByCompany_IdAndCreatedAtBetween(Integer companyId, LocalDateTime from, LocalDateTime to);
}
