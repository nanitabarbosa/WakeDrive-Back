package com.wakedrive.backend.device.repository;

import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.device.entity.Device;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {

    Optional<Device> findBySerial(String serial);

    List<Device> findByCompany_IdAndIdNotIn(Long companyId, List<Long> excludedIds);

    List<Device> findByCompany_IdAndStatusAndIdNotIn(Long companyId, RecordStatus status, List<Long> excludedIds);

    List<Device> findByCompany_Id(Long companyId);

    long countByCompany_Id(Long companyId);

    /** Total de dispositivos de varias empresas en una sola consulta (las empresas sin dispositivos no aparecen). */
    @Query("select d.company.id as companyId, count(d) as total from Device d "
            + "where d.company.id in :companyIds group by d.company.id")
    List<CompanyDeviceTotal> countByCompanyIds(@Param("companyIds") Collection<Long> companyIds);

    interface CompanyDeviceTotal {

        Long getCompanyId();

        long getTotal();
    }

    long countByCompany_IdAndStatus(Long companyId, RecordStatus status);
}
