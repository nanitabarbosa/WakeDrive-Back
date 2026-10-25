-- Esquema de empresas y solicitudes: estado y ciudad de la empresa y campos de auditoría (fechas y autor)
-- Idempotente: se puede ejecutar más de una vez sin error. Debe correr ANTES de 01_auditoria.sql (sus triggers leen updated_by).

-- ids y tamaños de archivo: INTEGER (antes BIGINT). Convierte las tablas que ya existan; si ya son INTEGER no hace nada.
-- Un trigger que nombra una columna (UPDATE OF / WHEN) impide cambiarle el tipo: se quitan y 01_auditoria.sql y 02_triggers_negocio.sql los vuelven a crear.
DROP TRIGGER IF EXISTS trg_audi_companies_update ON public.companies;
DROP TRIGGER IF EXISTS trg_validate_vinculation ON public.vinculations;

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

-- companies: estado (ACTIVE/INACTIVE) y ciudad. Las empresas existentes quedan ACTIVE.
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE';
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

-- companies: fechas y autor (correo del usuario que creó y que modificó por última vez).
-- Las empresas existentes quedan con la fecha de esta migración y sin autor.
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS created_at TIMESTAMP(6) NOT NULL DEFAULT now();
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP(6) NOT NULL DEFAULT now();
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- access_requests: fecha y autor de la última modificación (quién aprobó o rechazó).
-- La fecha de las solicitudes existentes se inicializa con su fecha de creación, solo la primera vez.
ALTER TABLE public.access_requests ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema = 'public' AND table_name = 'access_requests' AND column_name = 'updated_at') THEN
        ALTER TABLE public.access_requests ADD COLUMN updated_at TIMESTAMP(6);
        UPDATE public.access_requests SET updated_at = created_at;
        ALTER TABLE public.access_requests ALTER COLUMN updated_at SET NOT NULL;
        ALTER TABLE public.access_requests ALTER COLUMN updated_at SET DEFAULT now();
    END IF;
END
$$;
