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
        throw new ResourceNotFoundException("No authenticated user in context");
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equalsIgnoreCase(getCurrentUser().getRole().getName());
    }

    public Long getCurrentCompanyId() {
        User user = getCurrentUser();
        if (user.getCompany() == null) {
            throw new ResourceNotFoundException("Current user has no company assigned");
        }
        return user.getCompany().getId();
    }
}
