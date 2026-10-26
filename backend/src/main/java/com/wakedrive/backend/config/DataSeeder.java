package com.wakedrive.backend.config;

import com.wakedrive.backend.common.util.TextNormalizer;
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
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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

        if (userRepository.findByEmail(TextNormalizer.email(TextNormalizer.email(superAdminEmail))).isEmpty()) {
            if (superAdminPassword == null || superAdminPassword.isBlank()) {
                throw new IllegalStateException("app.seed.super-admin-password must not be blank");
            }
            User superAdmin = User.builder()
                    .name("Super Admin")
                    .email(TextNormalizer.email(superAdminEmail))
                    .password(passwordEncoder.encode(superAdminPassword))
                    .role(superAdminRole)
                    .build();
            userRepository.save(superAdmin);
        }

        List<String> cities = List.of(
                "Cúcuta", "Ocaña", "Pamplona", "Villa del Rosario", "Los Patios",
                "El Zulia", "Tibú", "Convención", "Ábrego", "Sardinata"
        );
        seedMissing(cities, cityRepository.findAll().stream().map(City::getName).collect(Collectors.toSet()),
                name -> cityRepository.save(City.builder().name(name).build()));

        List<String> eventTypes = List.of("DROWSINESS", "FACE_NOT_DETECTED", "DEVICE_OFF", "CONNECTION_LOST");
        seedMissing(eventTypes, eventTypeRepository.findAll().stream().map(EventType::getName).collect(Collectors.toSet()),
                name -> eventTypeRepository.save(EventType.builder().name(name).build()));

        renameVehicleTypes(Map.of(
                "TRUCK", "Camión",
                "TRACTOR_TRAILER", "Tractomula",
                "BUS", "Bus",
                "VAN", "Camioneta"));

        List<String> vehicleTypes = List.of("Camión", "Tractomula", "Bus", "Camioneta");
        seedMissing(vehicleTypes, vehicleTypeRepository.findAll().stream().map(VehicleType::getName).collect(Collectors.toSet()),
                name -> vehicleTypeRepository.save(VehicleType.builder().name(name).build()));

        List<String> brands = List.of(
                "Chevrolet", "Hino", "International", "Freightliner",
                "Kenworth", "Volvo", "Mercedes-Benz", "Isuzu",
                "Scania", "Mack", "Ford", "Toyota", "Renault", "Nissan",
                "JAC", "Foton", "Hyundai", "Kia", "Mazda", "Volkswagen",
                "Dongfeng", "Iveco", "Peterbilt", "DAF", "MAN", "Fiat"
        );
        seedMissing(brands, brandRepository.findAll().stream().map(Brand::getName).collect(Collectors.toSet()),
                name -> brandRepository.save(Brand.builder().name(name).build()));
    }

    private void renameVehicleTypes(Map<String, String> oldToNew) {
        vehicleTypeRepository.findAll().forEach(type -> {
            String newName = oldToNew.get(type.getName());
            if (newName != null) {
                type.setName(newName);
                vehicleTypeRepository.save(type);
            }
        });
    }

    private void seedMissing(List<String> wanted, Set<String> existing, Consumer<String> save) {
        wanted.stream().filter(name -> !existing.contains(name)).forEach(save);
    }

    private Role ensureRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
    }
}
