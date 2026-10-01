package com.wakedrive.backend.repository;

import com.wakedrive.backend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
