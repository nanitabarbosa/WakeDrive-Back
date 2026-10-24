package com.wakedrive.backend.company.repository;

import com.wakedrive.backend.company.entity.Company;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    /** Trae la ciudad junto con la página: sin esto, mostrar la ciudad de cada empresa haría una consulta por empresa. */
    @Override
    @EntityGraph(attributePaths = "city")
    Page<Company> findAll(Specification<Company> spec, Pageable pageable);

    Optional<Company> findByNit(String nit);

    boolean existsByNit(String nit);

    @Query("select count(c) > 0 from Company c where replace(c.nit, '-', '') = :nit")
    boolean existsByNormalizedNit(@Param("nit") String nit);
}
