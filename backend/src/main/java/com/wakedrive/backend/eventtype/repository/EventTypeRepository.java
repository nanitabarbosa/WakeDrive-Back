package com.wakedrive.backend.eventtype.repository;

import com.wakedrive.backend.eventtype.entity.EventType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Integer> {

    Optional<EventType> findByName(String name);
}
