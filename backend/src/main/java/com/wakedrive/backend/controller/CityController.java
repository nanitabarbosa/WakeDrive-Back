package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.CityDTO;
import com.wakedrive.backend.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
