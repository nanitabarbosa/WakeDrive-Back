# 03 — Services

Una interfaz `XxxService` en `com.wakedrive.backend.service` y su implementación `XxxServiceImpl` en `com.wakedrive.backend.service.impl`, para cada tabla. La conversión Entity↔DTO ocurre aquí (nunca en el Controller), según CLAUDE.md.

## Qué se creó

19 servicios, uno por entidad de `docs/01-entities.md`. La mayoría sigue el CRUD estándar (`getAll`, `getById`, `create`, `update`, `delete`), con estas excepciones deliberadas:

| Servicio | Diferencia con el CRUD estándar | Motivo |
|---|---|---|
| `RolePermissionService` | `getByRole` / `assign` / `revoke` en vez de CRUD por id (la entidad tiene clave compuesta) | Es una tabla puente; el caso de uso natural es "asignar/quitar un permiso a un rol", no editar la fila |
| `RefreshTokenService` | Sin Controller propio; sólo lo consume `AuthService` internamente (Fase de Seguridad) | Exponer CRUD REST sobre tokens de sesión permitiría forjar/leer tokens de otros usuarios |
| `EmailService` | Sin `update` | Un correo ya enviado (o su intento) es un registro de auditoría; no tiene sentido editarlo después de creado |
| `DrowsinessEventService` | Sin `update`, incluye `getByDriver` | Un evento de somnolencia detectado es un hecho inmutable; solo se crea o se elimina (ej. por error de hardware) |
| `DeviceConfigurationService` | Incluye `getByDeviceId` | Caso de uso frecuente: consultar la configuración vigente de un dispositivo específico |

## Decisiones de diseño

- **Validaciones de negocio en el servicio**: duplicados (`existsByUsername`, `findByLicensePlate`, `findBySerialNumber`, `findByIdentification`, etc.) y existencia de relaciones FK (`ResourceNotFoundException`) se validan aquí, no en el controller ni con constraints de base de datos únicamente.
- **Hash de contraseña**: `UserServiceImpl` usa el `PasswordEncoder` (BCrypt) de `PasswordEncoderConfig` para nunca persistir la contraseña en texto plano. En `update`, la contraseña solo se re-hashea si se envía un valor nuevo no vacío.
- **`EmailServiceImpl.create`**: inicializa `sent=false` y `countSent=0` por defecto, ya que el diagrama no marca esos campos como `NN` pero un correo recién creado no debería nacer marcado como "ya enviado".
- **`@Transactional`**: cada servicio es transaccional; los métodos de solo lectura usan `@Transactional(readOnly = true)` como buena práctica estándar de Spring.

## Supuestos

- Se asume que actualizar `RolePermission` no tiene sentido (solo asignar/revocar) porque no tiene más atributos que las dos FK.
- Se asume que `Email` y `DrowsinessEvent` son de solo creación/lectura/borrado; si se necesita edición (ej. reintentar un correo fallido cambiando su estado), avisar para agregar un método específico en vez de un `update` genérico.
