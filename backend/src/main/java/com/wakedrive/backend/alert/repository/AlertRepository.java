package com.wakedrive.backend.alert.repository;

import com.wakedrive.backend.alert.entity.Alert;
import com.wakedrive.backend.alert.entity.AlertLevel;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlertRepository extends JpaRepository<Alert, Integer>, JpaSpecificationExecutor<Alert> {

    List<Alert> findByDevice_IdAndLevel(Integer deviceId, AlertLevel level);

    boolean existsByDevice_IdAndLevel(Integer deviceId, AlertLevel level);

    /** Última alerta del dispositivo: de ahí se toma la ubicación conocida cuando se pierde la señal. */
    Optional<Alert> findFirstByDevice_IdOrderByDetectedAtDesc(Integer deviceId);

    long countByDriver_IdAndEventType_NameAndDetectedAtBetween(Integer driverId, String eventTypeName,
                                                               LocalDateTime from, LocalDateTime to);

    boolean existsByDriver_IdAndEventType_NameInAndDetectedAtBetween(Integer driverId, Collection<String> eventTypeNames,
                                                                     LocalDateTime from, LocalDateTime to);
}
