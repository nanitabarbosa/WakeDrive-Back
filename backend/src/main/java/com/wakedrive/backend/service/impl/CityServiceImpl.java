package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.CityDTO;
import com.wakedrive.backend.entity.City;
import com.wakedrive.backend.repository.CityRepository;
import com.wakedrive.backend.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<CityDTO> getAll() {
        return cityRepository.findAll().stream()
                .sorted(Comparator.comparing(City::getName))
                .map(city -> CityDTO.builder().id(city.getId()).name(city.getName()).build())
                .toList();
    }
}
