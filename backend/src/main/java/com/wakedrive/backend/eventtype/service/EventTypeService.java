package com.wakedrive.backend.eventtype.service;

import com.wakedrive.backend.eventtype.dto.EventTypeDTO;
import java.util.List;

public interface EventTypeService {

    List<EventTypeDTO> getAll();
}
