# 05 — Empresas, auditoría y endurecimiento

## Qué se creó o cambió

- **Seguridad de contadores:** `SecurityConfig` ahora protege `/api/companies/*/devices/**` (antes solo `/devices`), así que `.../devices/counts` es solo SUPER_ADMIN. También protege `/api/companies/*/status`.
- **Estado y ciudad de la empresa:** `Company.status` (`RecordStatus`, por defecto `ACTIVE`) y `Company.city` (FK a `cities`). `approve` copia la ciudad de la solicitud.
- **Activar o desactivar empresas:** `PUT /api/companies/{id}/status` con `{"status": "ACTIVE|INACTIVE"}`, solo SUPER_ADMIN. Devuelve el `CompanySummaryDTO` actualizado.
- **Empresa inactiva:** sus usuarios no pueden iniciar sesión ni renovar la sesión (403 `COMPANY_INACTIVE`) y sus tokens vigentes dejan de servir. El SUPER_ADMIN no se ve afectado.
- **Auditoría de empresas y solicitudes:**
  - `Company` gana `created_at`, `updated_at`, `created_by` y `updated_by`. `AccessRequest` gana `updated_at` y `updated_by`.
  - `JpaAuditingConfig` llena el autor con el correo del usuario autenticado.
  - Tablas `audi_companies` y `audi_access_requests` con triggers, con el mismo patrón OLD/NEW de `audi_users`.
- **Resumen de empresas (`getSummary`):** ordenado por nombre y luego id, busca por nombre o NIT (con o sin guion), trae la ciudad con la página y cuenta los dispositivos en una sola consulta. La respuesta añade `nit`, `city` y `status`.
- **Tope de página:** `PageRequests` limita `size` a 100 en todos los listados paginados.
- **Limpieza:** se elimina `CompanyRequestDTO` (ningún código lo usaba y el controlador no crea ni edita empresas con él).

## Decisiones de diseño

- El trigger de auditoría toma el autor de `updated_by` (lo escribe la app), porque la conexión a la BD es siempre el mismo usuario y `current_user` no dice quién actuó. Si el cambio no viene de la app (SQL directo) o es un `DELETE`, queda el usuario de la BD.
- Los `UPDATE` de `companies` solo se auditan si cambia un dato auditado (NIT, nombre, dirección, teléfono, correo, ciudad o estado). Guardar el logo o la alarma no genera filas.
- La contraseña se valida antes de avisar que la empresa está inactiva, para no revelar a quien no la conoce que ese correo existe.
- Las columnas nuevas van en `sql/00_esquema.sql` porque `ddl-auto=none`. Debe correr antes de `01_auditoria.sql`.

## Supuestos y pendientes

- **Aplicar a una base existente:** correr `update.sql` antes de arrancar el backend. Sin las columnas nuevas, la app falla al leer `companies`.
- `companies.city_id` queda nullable. Se rellena desde la solicitud aprobada con el mismo NIT. Revisar con `SELECT * FROM companies WHERE city_id IS NULL`; si no queda ninguna, se puede volver `NOT NULL`.
- Las empresas existentes quedan con `created_at` igual a la fecha de la migración y sin `created_by`.
- Cambios visibles para el frontend:
  - Campos nuevos en `/api/companies/summary`.
  - Endpoint nuevo de estado.
  - `/api/companies/{id}/devices/counts` ahora responde 403 a quien no sea SUPER_ADMIN.
  - Máximo de 100 por página: un `size` mayor se recorta a 100.
- Flyway no se introdujo. `FileStorageServiceImplTest` tiene 3 pruebas que esperan `IllegalArgumentException` y el servicio ahora lanza `InvalidFileException` (viene del commit `dc3f6ef`).
