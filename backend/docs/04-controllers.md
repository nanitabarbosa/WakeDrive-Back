# 04 — Controllers

Un `@RestController` por recurso en `com.wakedrive.backend.controller`, bajo el prefijo `/api`. Todos reciben y devuelven DTOs exclusivamente (nunca entidades), delegando toda la lógica al Service correspondiente.

## Qué se creó

| Recurso | Ruta base | Notas |
|---|---|---|
| Módulos | `/api/modules` | CRUD estándar |
| Permisos | `/api/permissions` | CRUD estándar |
| Roles | `/api/roles` | CRUD estándar |
| Asignación de permisos a roles | `/api/roles/{roleId}/permissions` | `GET` lista, `POST` asigna, `DELETE /{permissionId}` revoca (tabla puente, no CRUD por id) |
| Usuarios | `/api/users` | CRUD estándar |
| Personas | `/api/persons` | CRUD estándar |
| Empresas | `/api/companies` | Clave natural `nit` en la URL en vez de un UUID |
| Marcas | `/api/brands` | CRUD estándar |
| Modelos de vehículo | `/api/vehicle-models` | CRUD estándar |
| Tipos de vehículo | `/api/vehicle-types` | CRUD estándar |
| Vehículos | `/api/vehicles` | CRUD estándar |
| Dispositivos | `/api/devices` | CRUD estándar |
| Configuración de dispositivo | `/api/device-configurations` | Incluye `GET /by-device/{deviceId}` |
| Conductores | `/api/drivers` | CRUD estándar |
| Tipos de evento | `/api/event-types` | CRUD estándar |
| Eventos de somnolencia | `/api/drowsiness-events` | Sin `PUT` (evento inmutable); incluye `GET /by-driver/{driverId}` |
| Correos | `/api/emails` | Sin `PUT` (registro de auditoría) |
| Plantillas de correo | `/api/email-templates` | Clave natural `name` en la URL |

**`refresh_token` no tiene controller**: solo se usa internamente desde el flujo de autenticación (ver `docs/05-security.md`), para no exponer un endpoint que permita leer o crear tokens de sesión de otros usuarios directamente.

## Decisiones de diseño

- Todos los endpoints de creación devuelven `201 Created` (`@ResponseStatus(HttpStatus.CREATED)`) y los de borrado `204 No Content`.
- Validación de payloads con `@Valid` sobre los `XxxRequestDTO`; los errores de validación los captura `GlobalExceptionHandler` (creado en la Fase 1) y se devuelven como `400` con el detalle de cada campo.
- No se agregó paginación (`Pageable`) todavía: no fue pedida y añadiría complejidad no solicitada; se puede agregar después si las listas crecen mucho.
- `RolePermissionController` anida bajo `/api/roles/{roleId}` porque conceptualmente es una operación sobre el rol ("gestionar los permisos de este rol"), no un recurso independiente con su propio ID.

## Supuestos

- Se asume que todos los `GET` de listado son públicos de lectura a nivel de estructura, y que la restricción real de acceso por rol/empresa se resolverá en la Fase de Seguridad (Fase 6) con Spring Security, no en el Controller.
