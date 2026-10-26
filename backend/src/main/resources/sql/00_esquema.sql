-- Esquema de empresas y solicitudes: estado y ciudad de la empresa
-- Idempotente: se puede ejecutar más de una vez sin error. Debe correr ANTES de 01_auditoria.sql.

-- ids y tamaños de archivo: INTEGER (antes BIGINT). Convierte las tablas que ya existan; si ya son INTEGER no hace nada.
-- Un trigger que nombra una columna (UPDATE OF / WHEN) impide cambiarle el tipo: se quitan y 01_auditoria.sql y 02_triggers_negocio.sql los vuelven a crear.
DROP TRIGGER IF EXISTS trg_audi_companies_update ON public.companies;
DROP TRIGGER IF EXISTS trg_validate_vinculation ON public.vinculations;

-- companies ya no guarda fechas ni autor (created_at, updated_at, created_by y updated_by): se eliminan de las bases que todavía los tengan.
ALTER TABLE public.companies DROP COLUMN IF EXISTS created_at, DROP COLUMN IF EXISTS updated_at, DROP COLUMN IF EXISTS created_by, DROP COLUMN IF EXISTS updated_by;
-- access_requests tampoco guarda la fecha ni el autor de la última modificación (updated_at y updated_by).
ALTER TABLE public.access_requests DROP COLUMN IF EXISTS updated_at, DROP COLUMN IF EXISTS updated_by;

DO $$
DECLARE
    t RECORD;
BEGIN
    FOR t IN
        SELECT c.table_name,
               string_agg(format('ALTER COLUMN %I TYPE INTEGER', c.column_name), ', ' ORDER BY c.ordinal_position) AS cambios
        FROM information_schema.columns c
        JOIN pg_tables p ON p.schemaname = c.table_schema AND p.tablename = c.table_name
        WHERE c.table_schema = 'public' AND c.data_type = 'bigint'
        GROUP BY c.table_name
    LOOP
        EXECUTE format('ALTER TABLE public.%I %s', t.table_name, t.cambios);
    END LOOP;
END
$$;

-- drivers.phone es obligatorio (la aplicación ya lo exigía). Solo se marca NOT NULL si ningún conductor está sin teléfono.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM public.drivers WHERE phone IS NULL) THEN
        RAISE NOTICE 'drivers.phone sigue opcional: hay conductores sin teléfono; completarlos y volver a ejecutar este script';
    ELSE
        ALTER TABLE public.drivers ALTER COLUMN phone SET NOT NULL;
    END IF;
END
$$;

-- companies: estado (ACTIVE/INACTIVE) y ciudad. Las empresas existentes quedan ACTIVE.
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
-- En las bases que ya tenían la columna con 255 caracteres, se ajusta a 20 (como el estado de los demás registros).
ALTER TABLE public.companies ALTER COLUMN status TYPE VARCHAR(20);
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS city_id INTEGER REFERENCES public.cities (city_id);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_companies_status') THEN
        ALTER TABLE public.companies ADD CONSTRAINT ck_companies_status CHECK (status IN ('ACTIVE', 'INACTIVE'));
    END IF;
END
$$;

-- La ciudad de las empresas existentes se recupera de su solicitud de acceso aprobada (se cruza por NIT sin guion).
-- Las empresas sin solicitud aprobada quedan con city_id nulo.
UPDATE public.companies c
SET city_id = ar.city_id
FROM public.access_requests ar
WHERE c.city_id IS NULL
  AND ar.status = 'APPROVED'
  AND replace(ar.nit, '-', '') = replace(c.nit, '-', '');

-- companies.city_id es obligatorio. Solo se marca NOT NULL si ninguna empresa quedó sin ciudad.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM public.companies WHERE city_id IS NULL) THEN
        RAISE NOTICE 'companies.city_id sigue opcional: hay empresas sin ciudad; completarlas y volver a ejecutar este script';
    ELSE
        ALTER TABLE public.companies ALTER COLUMN city_id SET NOT NULL;
    END IF;
END
$$;

