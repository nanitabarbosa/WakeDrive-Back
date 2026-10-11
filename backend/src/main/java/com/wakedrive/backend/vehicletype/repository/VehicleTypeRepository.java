package com.wakedrive.backend.vehicletype.repository;

import com.wakedrive.backend.vehicletype.entity.VehicleType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {

    Optional<VehicleType> findByName(String name);
}
