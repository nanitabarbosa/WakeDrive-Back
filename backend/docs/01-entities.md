# 01 — Entidades JPA

Todas las tablas del diagrama físico están implementadas como entidades en `com.wakedrive.backend.entity`. Se excluyeron los campos marcados en rosado: `USERS.status`, `COMPANY.identification`, `COMPANY.status`, `VEHICLE.status`, `DRIVER.status`, `DEVICE.status`, `DEVICE_CONFIGURATION.ear_threshold`, `DROWSINESS_EVENT.duration`, `DROWSINESS_EVENT.severity`.

## Qué se creó

| Tabla del diagrama | Clase Java | Notas |
|---|---|---|
| MODULES | `Module` | Self-FK `parent` (árbol de menú) |
| PERMISSIONS | `Permission` | FK a `Module` |
| ROLES | `Role` | — |
| role_permission | `RolePermission` + `RolePermissionId` | Clave compuesta (`@IdClass`) |
| USERS | `User` | FK a `Role` y a `Company` (`nit`) |
| PERSONS | `Person` | `@OneToOne` con `User` |
| refresh_token | `RefreshToken` | FK a `User` |
| COMPANY | `Company` | PK natural `nit` (no autogenerada) |
| BRAND | `Brand` | — |
| VEHICLE_MODELS | `VehicleModel` | FK a `Brand` |
| TYPE | `VehicleType` | Renombrada a `VehicleType` (evita colisión con `java.lang.reflect.Type` / palabra reservada poco descriptiva) |
| VEHICLE | `Vehicle` | FK a `VehicleModel`, `VehicleType`, `Company` |
| DEVICE | `Device` | FK a `Vehicle` |
| DEVICE_CONFIGURATION | `DeviceConfiguration` | `@OneToOne` con `Device` (confirmado con el usuario) |
| DRIVER | `Driver` | FK a `Vehicle` |
| EVENT_TYPE | `EventType` | — |
| DROWSINESS_EVENT | `DrowsinessEvent` | FK a `Driver`, `EventType`, `Device` |
| email | `Email` | Sin relaciones (no tiene FK en el diagrama) |
| email_template | `EmailTemplate` | PK natural `name` |

## Decisiones de diseño

- **IDs**: `UUID` autogenerado (`@GeneratedValue`, estrategia por defecto de Hibernate 6) en todas las tablas, excepto las claves naturales ya definidas en el diagrama (`Company.nit`, `EmailTemplate.name`) y la clave compuesta de `RolePermission`.
- **Lombok**: `@Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor/@Builder` en todas las entidades para evitar boilerplate.
- **Fetch type**: todas las relaciones `@ManyToOne`/`@OneToOne` usan `FetchType.LAZY` para evitar cargas innecesarias (buena práctica estándar en JPA).
- **`DEVICE_CONFIGURATION` ↔ `DEVICE`**: se implementó como 1 a 1 con restricción única sobre `device_id`, confirmado con el usuario aunque el diagrama no marcaba esa FK como `UQ`.
- **`TYPE` → `VehicleType`**: se renombró la clase Java (la tabla sigue llamándose `type` vía `@Table(name = "type")`) porque `Type` es un nombre poco descriptivo y colisiona con `java.lang.reflect.Type`.

## Supuestos (a revisar si no son correctos)

- `Vehicle.licensePlate` y `Device.serialNumber` se marcaron `unique = true` aunque el diagrama solo los marca `NN` (no `UQ`). Es una restricción de negocio razonable (placas y números de serie no deberían duplicarse), pero se documenta como una adición sobre lo explícito en el diagrama.
- `Driver.identification` no tenía tipo/longitud especificado en el diagrama; se usó `VARCHAR(50)` con restricción única.
- `DeviceConfiguration.closedEyeTime` se implementó literalmente como `TIMESTAMP` (`LocalDateTime`) tal como aparece en el diagrama, aunque semánticamente parece pensado como una duración (ej. segundos con los ojos cerrados antes de alarmar). Se dejó tal cual para no reinterpretar el modelo sin confirmación; si se esperaba un `Integer` (segundos), avisar para corregirlo.
- `Email.sent` y `Email.countSent` se dejaron nullable (el diagrama no los marca `NN`); en la capa de servicio se puede aplicar un valor por defecto (`false` / `0`) al crear el registro.
