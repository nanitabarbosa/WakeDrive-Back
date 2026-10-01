# Plan de implementación — WakeDrive Backend

Basado en el diagrama físico entregado. Se excluyen del modelo los campos marcados en rosado: `USERS.status`, `COMPANY.identification`, `COMPANY.status`, `VEHICLE.status`, `DRIVER.status`, `DEVICE.status`, `DEVICE_CONFIGURATION.ear_threshold`, `DROWSINESS_EVENT.duration`, `DROWSINESS_EVENT.severity`.

## Convenciones generales (aplican a todas las fases)

- Paquete base: `com.wakedrive.backend`, organizado por capa: `entity`, `repository`, `service`, `service.impl`, `controller`, `dto`, `config`, `security`, `exception`.
- IDs: `UUID` autogenerado (`GenerationType.UUID`) en todas las tablas, excepto claves naturales ya definidas en el diagrama: `COMPANY.nit`, `email_template.name`, y la clave compuesta `role_permission (role_id, permission_id)`.
- Cada entidad tiene su propio DTO de respuesta (`XxxDTO`) y, cuando el payload de creación/edición difiere, un `XxxRequestDTO`. La conversión Entity↔DTO vive en el Service.
- Lombok se usa para reducir boilerplate (`@Getter/@Setter`, `@Builder`, constructores) en entidades y DTOs.
- Persistencia de esquema: `spring.jpa.hibernate.ddl-auto=update` (no se introduce Flyway/Liquibase salvo que se pida).
- `DEVICE_CONFIGURATION` es 1 a 1 con `DEVICE` (confirmado con el usuario): se agrega restricción única sobre `device_id`.
- Cada fase termina con un reporte en `docs/0X-nombre.md` con lo creado, decisiones de diseño y supuestos.

## Fases

### Fase 1 — Seguridad y usuarios (RBAC + perfil + sesión)
Tablas: `MODULES`, `PERMISSIONS`, `ROLES`, `role_permission`, `USERS`, `PERSONS`, `refresh_token`.
- `MODULES` es un árbol (self-FK `parent_id`) para el menú de navegación.
- `PERMISSIONS` pertenece a un `MODULE`; `role_permission` es la tabla puente Roles↔Permissions (clave compuesta).
- `USERS` referencia `ROLES` y `COMPANY` (`nit`); `PERSONS` es un perfil 1 a 1 de `USERS`.
- `refresh_token` soporta la renovación de sesión JWT (implementación de seguridad se hace en Fase 6).

### Fase 2 — Empresa y vehículos
Tablas: `COMPANY`, `BRAND`, `VEHICLE_MODELS`, `TYPE`, `VEHICLE`.
- `VEHICLE_MODELS` referencia `BRAND`; `VEHICLE` referencia `VEHICLE_MODELS`, `TYPE` y `COMPANY` (`nit`).

### Fase 3 — Dispositivos
Tablas: `DEVICE`, `DEVICE_CONFIGURATION`.
- `DEVICE` referencia `VEHICLE`. `DEVICE_CONFIGURATION` es 1 a 1 con `DEVICE`.

### Fase 4 — Conductores y eventos de somnolencia
Tablas: `DRIVER`, `EVENT_TYPE`, `DROWSINESS_EVENT`.
- `DRIVER` referencia `VEHICLE`. `DROWSINESS_EVENT` referencia `DRIVER`, `EVENT_TYPE` y `DEVICE`.

### Fase 5 — Notificaciones
Tablas: `email`, `email_template`.
- No tienen FK explícitas en el diagrama hacia otras tablas; se implementan como catálogo/log independiente para uso futuro del envío de correos.

### Fase 6 — Seguridad de la aplicación (JWT)
- Spring Security + filtro JWT (access token + refresh token vía tabla `refresh_token`).
- `BCryptPasswordEncoder` para `USERS.password`.
- Endpoints de autenticación: login, refresh, logout.
- Autorización a nivel de método basada en el nombre del `ROLE` (no se implementa chequeo fino por `PERMISSIONS` en esta fase salvo que se pida explícitamente).

### Fase 7 — Documentación final
- `docs/07-security.md` y revisión general de `docs/00-plan.md` con el estado final "Implementado".

## Pendiente de confirmación

Ninguno adicional por ahora — si aparece una ambigüedad de arquitectura durante una fase, se detiene la ejecución y se pregunta antes de continuar.
