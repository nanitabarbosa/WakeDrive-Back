package com.wakedrive.backend.device.repository;

import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.device.entity.Device;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {

    Optional<Device> findBySerial(String serial);

    List<Device> findByCompany_IdAndIdNotIn(Long companyId, List<Long> excludedIds);

    List<Device> findByCompany_Id(Long companyId);

    long countByCompany_Id(Long companyId);

    long countByCompany_IdAndStatus(Long companyId, RecordStatus status);
}
