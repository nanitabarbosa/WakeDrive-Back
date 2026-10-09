package com.wakedrive.backend.config;

import com.wakedrive.backend.city.entity.City;
import com.wakedrive.backend.city.repository.CityRepository;
import com.wakedrive.backend.user.entity.Role;
import com.wakedrive.backend.user.entity.User;
import com.wakedrive.backend.user.repository.RoleRepository;
import com.wakedrive.backend.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.super-admin-email:superadmin@wakedrive.com}")
    private String superAdminEmail;

    @Value("${app.seed.super-admin-password:SuperAdmin123!}")
    private String superAdminPassword;

    @Override
    public void run(String... args) {
        Role superAdminRole = ensureRole("SUPER_ADMIN");
        ensureRole("ADMIN");

        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            User superAdmin = User.builder()
                    .name("Super Admin")
                    .email(superAdminEmail)
                    .password(passwordEncoder.encode(superAdminPassword))
                    .role(superAdminRole)
                    .build();
            userRepository.save(superAdmin);
        }

        if (cityRepository.count() == 0) {
            List<String> cities = List.of(
                    "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena",
                    "Bucaramanga", "Pereira", "Santa Marta", "Manizales", "Cúcuta"
            );
            cities.forEach(name -> cityRepository.save(City.builder().name(name).build()));
        }
    }

    private Role ensureRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
    }
}
