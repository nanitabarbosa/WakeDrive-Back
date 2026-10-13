package com.wakedrive.backend.company.repository;

import com.wakedrive.backend.company.entity.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    Optional<Company> findByNit(String nit);

    boolean existsByNit(String nit);

    @Query("select count(c) > 0 from Company c where replace(c.nit, '-', '') = :nit")
    boolean existsByNormalizedNit(@Param("nit") String nit);

    @Query("select count(c) > 0 from Company c where replace(c.nit, '-', '') = :nit and c.id <> :id")
    boolean existsByNormalizedNitAndIdNot(@Param("nit") String nit, @Param("id") Long id);
}
