package com.wakedrive.backend.brand.repository;

import com.wakedrive.backend.brand.entity.Brand;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    Optional<Brand> findByName(String name);
}
