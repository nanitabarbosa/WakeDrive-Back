-- Esquema de empresas y solicitudes: estado y ciudad de la empresa y campos de auditoría (fechas y autor)
-- Idempotente: se puede ejecutar más de una vez sin error. Debe correr ANTES de 01_auditoria.sql (sus triggers leen updated_by).

-- companies: estado (ACTIVE/INACTIVE) y ciudad. Las empresas existentes quedan ACTIVE.
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE public.companies ADD COLUMN IF NOT EXISTS city_id BIGINT REFERENCES public.cities (city_id);

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
