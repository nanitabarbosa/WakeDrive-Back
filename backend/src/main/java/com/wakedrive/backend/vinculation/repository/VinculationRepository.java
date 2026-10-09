package com.wakedrive.backend.vinculation.repository;

import com.wakedrive.backend.vinculation.entity.Vinculation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VinculationRepository extends JpaRepository<Vinculation, Long>, JpaSpecificationExecutor<Vinculation> {

    Optional<Vinculation> findByDevice_Serial(String serial);
}
