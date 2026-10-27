package com.wakedrive.backend.device.repository;

import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.device.entity.Device;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceRepository extends JpaRepository<Device, Integer>, JpaSpecificationExecutor<Device> {

    Optional<Device> findBySerial(String serial);

    List<Device> findByCompany_IdAndIdNotIn(Integer companyId, List<Integer> excludedIds);

    List<Device> findByCompany_IdAndStatusAndIdNotIn(Integer companyId, RecordStatus status, List<Integer> excludedIds);

    List<Device> findByCompany_Id(Integer companyId);

    long countByCompany_Id(Integer companyId);

    /** Total de dispositivos de varias empresas en una sola consulta (las empresas sin dispositivos no aparecen). */
    @Query("select d.company.id as companyId, count(d) as total from Device d "
            + "where d.company.id in :companyIds group by d.company.id")
    List<CompanyDeviceTotal> countByCompanyIds(@Param("companyIds") Collection<Integer> companyIds);

    interface CompanyDeviceTotal {

        Integer getCompanyId();

        long getTotal();
    }

    long countByCompany_IdAndStatus(Integer companyId, RecordStatus status);

    /** Dispositivos que dejaron de reportarse antes de la fecha indicada. */
    List<Device> findByStatusAndLastConnectionBefore(RecordStatus status, LocalDateTime before);
}
