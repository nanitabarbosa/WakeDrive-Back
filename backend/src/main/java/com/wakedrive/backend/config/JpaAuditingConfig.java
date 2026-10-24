package com.wakedrive.backend.config;

import com.wakedrive.backend.security.CustomUserDetails;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Quién hizo el cambio, para los campos @CreatedBy y @LastModifiedBy: el correo del usuario autenticado.
     * Vacío en peticiones públicas (por ejemplo, el formulario de solicitud de acceso) y en procesos internos.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(principal -> ((CustomUserDetails) principal).getUser().getEmail());
    }
}
