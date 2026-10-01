package com.wakedrive.backend.repository;

import com.wakedrive.backend.entity.Vinculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VinculationRepository extends JpaRepository<Vinculation, Long>, JpaSpecificationExecutor<Vinculation> {

    Optional<Vinculation> findByDevice_Serial(String serial);
}
