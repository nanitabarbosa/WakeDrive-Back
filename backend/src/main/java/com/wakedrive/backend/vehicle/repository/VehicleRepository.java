package com.wakedrive.backend.vehicle.repository;

import com.wakedrive.backend.vehicle.entity.Vehicle;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByPlate(String plate);

    List<Vehicle> findByCompany_IdAndIdNotIn(Long companyId, List<Long> excludedIds);

    List<Vehicle> findByCompany_Id(Long companyId);

    long countByCompany_Id(Long companyId);

    long countByCompany_IdAndCreatedAtBetween(Long companyId, LocalDateTime from, LocalDateTime to);
}
