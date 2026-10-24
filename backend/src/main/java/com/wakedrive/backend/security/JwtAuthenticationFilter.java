package com.wakedrive.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_PATH_PREFIX = "/api/auth/";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            if (jwtService.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = loadUser(token);
                if (userDetails != null && hasActiveCompany(userDetails)) {
                    if (mustChangePassword(userDetails) && !request.getRequestURI().startsWith(AUTH_PATH_PREFIX)) {
                        rejectUntilPasswordChanged(response);
                        return;
                    }
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private UserDetails loadUser(String token) {
        try {
            return userDetailsService.loadUserByUsername(jwtService.extractEmail(token));
        } catch (UsernameNotFoundException e) {
            return null;
        }
    }

    /** Un token ya emitido deja de servir en cuanto se desactiva la empresa del usuario. */
    private boolean hasActiveCompany(UserDetails userDetails) {
        return !(userDetails instanceof CustomUserDetails details) || details.getUser().hasActiveCompany();
    }

    private boolean mustChangePassword(UserDetails userDetails) {
        return userDetails instanceof CustomUserDetails details
                && Boolean.TRUE.equals(details.getUser().getMustChangePassword());
    }

    private void rejectUntilPasswordChanged(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Password change required\"}");
    }
}
