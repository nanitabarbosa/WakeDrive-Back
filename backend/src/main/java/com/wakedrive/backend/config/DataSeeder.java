package com.wakedrive.backend.config;

import com.wakedrive.backend.brand.entity.Brand;
import com.wakedrive.backend.brand.repository.BrandRepository;
import com.wakedrive.backend.city.entity.City;
import com.wakedrive.backend.city.repository.CityRepository;
import com.wakedrive.backend.eventtype.entity.EventType;
import com.wakedrive.backend.eventtype.repository.EventTypeRepository;
import com.wakedrive.backend.user.entity.Role;
import com.wakedrive.backend.user.entity.User;
import com.wakedrive.backend.user.repository.RoleRepository;
import com.wakedrive.backend.user.repository.UserRepository;
import com.wakedrive.backend.vehicletype.entity.VehicleType;
import com.wakedrive.backend.vehicletype.repository.VehicleTypeRepository;
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
    private final EventTypeRepository eventTypeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final BrandRepository brandRepository;
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

        if (eventTypeRepository.count() == 0) {
            List<String> eventTypes = List.of("DROWSINESS", "FACE_NOT_DETECTED", "DEVICE_OFF", "CONNECTION_LOST");
            eventTypes.forEach(name -> eventTypeRepository.save(EventType.builder().name(name).build()));
        }

        if (vehicleTypeRepository.count() == 0) {
            List<String> vehicleTypes = List.of("TRUCK", "TRACTOR_TRAILER", "BUS", "VAN");
            vehicleTypes.forEach(name -> vehicleTypeRepository.save(VehicleType.builder().name(name).build()));
        }

        if (brandRepository.count() == 0) {
            List<String> brands = List.of(
                    "Chevrolet", "Hino", "International", "Freightliner",
                    "Kenworth", "Volvo", "Mercedes-Benz", "Isuzu"
            );
            brands.forEach(name -> brandRepository.save(Brand.builder().name(name).build()));
        }
    }

    private Role ensureRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
    }
}
