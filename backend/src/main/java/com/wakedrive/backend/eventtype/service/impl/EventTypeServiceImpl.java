package com.wakedrive.backend.eventtype.service.impl;

import com.wakedrive.backend.eventtype.dto.EventTypeDTO;
import com.wakedrive.backend.eventtype.entity.EventType;
import com.wakedrive.backend.eventtype.repository.EventTypeRepository;
import com.wakedrive.backend.eventtype.service.EventTypeService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventTypeServiceImpl implements EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    @Override
    public List<EventTypeDTO> getAll() {
        return eventTypeRepository.findAll().stream()
                .sorted(Comparator.comparing(EventType::getName))
                .map(eventType -> EventTypeDTO.builder().id(eventType.getId()).name(eventType.getName()).build())
                .toList();
    }
}
