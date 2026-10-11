package com.wakedrive.backend.eventtype.controller;

import com.wakedrive.backend.eventtype.dto.EventTypeDTO;
import com.wakedrive.backend.eventtype.service.EventTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @GetMapping
    public List<EventTypeDTO> getAll() {
        return eventTypeService.getAll();
    }
}
