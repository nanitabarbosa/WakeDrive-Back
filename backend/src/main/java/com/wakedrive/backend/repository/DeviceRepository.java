package com.wakedrive.backend.repository;

import com.wakedrive.backend.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {

    Optional<Device> findBySerial(String serial);

    List<Device> findByCompany_IdAndIdNotIn(Long companyId, List<Long> excludedIds);

    List<Device> findByCompany_Id(Long companyId);

    long countByCompany_Id(Long companyId);

    long countByCompany_IdAndStatus(Long companyId, com.wakedrive.backend.entity.RecordStatus status);
}
