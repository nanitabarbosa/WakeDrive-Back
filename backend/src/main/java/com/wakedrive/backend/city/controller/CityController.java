package com.wakedrive.backend.city.controller;

import com.wakedrive.backend.city.dto.CityDTO;
import com.wakedrive.backend.city.service.CityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public List<CityDTO> getAll() {
        return cityService.getAll();
    }
}
