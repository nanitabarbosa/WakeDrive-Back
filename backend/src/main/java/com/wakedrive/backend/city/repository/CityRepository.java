package com.wakedrive.backend.city.repository;

import com.wakedrive.backend.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
