# 05 — Empresas, auditoría y endurecimiento

## Qué se creó o cambió

- **Seguridad de contadores:** `SecurityConfig` ahora protege `/api/companies/*/devices/**` (antes solo `/devices`), así que `.../devices/counts` es solo SUPER_ADMIN. También protege `/api/companies/*/status`.
- **Estado y ciudad de la empresa:** `Company.status` (`RecordStatus`, por defecto `ACTIVE`) y `Company.city` (FK a `cities`). `approve` copia la ciudad de la solicitud.
- **Activar o desactivar empresas:** `PUT /api/companies/{id}/status` con `{"status": "ACTIVE|INACTIVE"}`, solo SUPER_ADMIN. Devuelve el `CompanySummaryDTO` actualizado.
- **Empresa inactiva:** sus usuarios no pueden iniciar sesión ni renovar la sesión (403 `COMPANY_INACTIVE`) y sus tokens vigentes dejan de servir. El SUPER_ADMIN no se ve afectado.
- **Auditoría de empresas y solicitudes:**
  - `Company` y `AccessRequest` no guardan fechas ni autor de la última modificación: siempre actúa el SUPER_ADMIN.
  - Solo se audita `users` (tabla `audi_users`). Las tablas `audi_vinculations` y `audi_companies` se retiraron para igualar el esquema desplegado en Supabase; `update.sql` y `01_auditoria.sql` las eliminan si existen.
- **Resumen de empresas (`getSummary`):** ordenado por nombre y luego id, busca por nombre o NIT (con o sin guion), trae la ciudad con la página y cuenta los dispositivos en una sola consulta. La respuesta añade `nit`, `city` y `status`.
- **Tope de página:** `PageRequests` limita `size` a 100 en todos los listados paginados.
- **Limpieza:** se elimina `CompanyRequestDTO` (ningún código lo usaba y el controlador no crea ni edita empresas con él).

## Decisiones de diseño

- El trigger de auditoría de `users` guarda en `changed_by` el usuario de la base de datos; como la aplicación se conecta siempre con el mismo usuario, no distingue quién actuó.
- La contraseña se valida antes de avisar que la empresa está inactiva, para no revelar a quien no la conoce que ese correo existe.
- Las columnas nuevas van en `sql/00_esquema.sql` porque `ddl-auto=none`. Debe correr antes de `01_auditoria.sql`.

## Supuestos y pendientes

- **Aplicar a una base existente:** correr `update.sql` antes de arrancar el backend. Sin las columnas nuevas, la app falla al leer `companies`.
- `companies.city_id` es obligatorio (`NOT NULL`). En bases existentes se rellena desde la solicitud aprobada con el mismo NIT y el script solo la marca `NOT NULL` si ninguna empresa queda sin ciudad.
- Cambios visibles para el frontend:
  - Campos nuevos en `/api/companies/summary`.
  - Endpoint nuevo de estado.
  - `/api/companies/{id}/devices/counts` ahora responde 403 a quien no sea SUPER_ADMIN.
  - Máximo de 100 por página: un `size` mayor se recorta a 100.
- Flyway no se introdujo. `FileStorageServiceImplTest` tiene 3 pruebas que esperan `IllegalArgumentException` y el servicio ahora lanza `InvalidFileException` (viene del commit `dc3f6ef`).
