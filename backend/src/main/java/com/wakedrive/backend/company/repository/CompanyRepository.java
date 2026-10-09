package com.wakedrive.backend.company.repository;

import com.wakedrive.backend.company.entity.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    Optional<Company> findByNit(String nit);

    boolean existsByNit(String nit);
}
