package com.wakedrive.backend.city.service.impl;

import com.wakedrive.backend.city.dto.CityDTO;
import com.wakedrive.backend.city.entity.City;
import com.wakedrive.backend.city.repository.CityRepository;
import com.wakedrive.backend.city.service.CityService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
