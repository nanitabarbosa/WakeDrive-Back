package com.wakedrive.backend.security;

import com.wakedrive.backend.common.exception.ResourceNotFoundException;
import com.wakedrive.backend.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }
        throw new ResourceNotFoundException("No hay un usuario autenticado");
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equalsIgnoreCase(getCurrentUser().getRole().getName());
    }

    public Integer getCurrentCompanyId() {
        User user = getCurrentUser();
        if (user.getCompany() == null) {
            throw new ResourceNotFoundException("Tu usuario no tiene una empresa asignada");
        }
        return user.getCompany().getId();
    }
}
