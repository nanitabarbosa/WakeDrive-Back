# 02 — Repositories

Un repositorio Spring Data JPA (`JpaRepository`) por entidad, en `com.wakedrive.backend.repository`. Cubren las 19 entidades de `docs/01-entities.md`.

## Qué se creó

| Repositorio | Entidad | Tipo de ID | Métodos derivados propios |
|---|---|---|---|
| `ModuleRepository` | `Module` | `UUID` | — |
| `PermissionRepository` | `Permission` | `UUID` | — |
| `RoleRepository` | `Role` | `UUID` | `findByName` |
| `RolePermissionRepository` | `RolePermission` | `RolePermissionId` (compuesta) | `findByRole_RoleId` |
| `UserRepository` | `User` | `UUID` | `findByUsername`, `existsByUsername` |
| `PersonRepository` | `Person` | `UUID` | `findByUser_UserId` |
| `RefreshTokenRepository` | `RefreshToken` | `UUID` | `findByToken`, `deleteByUser_UserId` |
| `CompanyRepository` | `Company` | `String` (`nit`) | — |
| `BrandRepository` | `Brand` | `UUID` | — |
| `VehicleModelRepository` | `VehicleModel` | `UUID` | `findByBrand_BrandId` |
| `VehicleTypeRepository` | `VehicleType` | `UUID` | — |
| `VehicleRepository` | `Vehicle` | `UUID` | `findByLicensePlate`, `findByCompany_Nit` |
| `DeviceRepository` | `Device` | `UUID` | `findBySerialNumber`, `findByVehicle_VehicleId` |
| `DeviceConfigurationRepository` | `DeviceConfiguration` | `UUID` | `findByDevice_DeviceId` |
| `DriverRepository` | `Driver` | `UUID` | `findByIdentification`, `findByVehicle_VehicleId` |
| `EventTypeRepository` | `EventType` | `UUID` | — |
| `DrowsinessEventRepository` | `DrowsinessEvent` | `UUID` | `findByDriver_DriverId`, `findByDevice_DeviceId` |
| `EmailRepository` | `Email` | `UUID` | — |
| `EmailTemplateRepository` | `EmailTemplate` | `String` (`name`) | — |

## Decisiones de diseño

- Todos son interfaces simples que extienden `JpaRepository<Entity, IdType>`; no se usó `JpaSpecificationExecutor` ni QueryDSL — no hay requisitos de filtrado dinámico complejo todavía, y CLAUDE.md pide evitar abstracciones no necesarias.
- Los métodos derivados (`findByX_Y`) siguen la convención estándar de Spring Data para navegar relaciones (ej. `findByVehicle_VehicleId` busca por el `vehicleId` del `Vehicle` asociado).
- No se agregaron métodos que no vayan a usarse todavía en el Service layer (ej. no se creó `findByRoute` en `ModuleRepository` porque no hay un caso de uso definido para eso).

## Supuestos

Ninguno adicional a los ya documentados en `docs/01-entities.md`.
