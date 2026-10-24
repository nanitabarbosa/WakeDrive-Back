-- Regla de negocio: las alertas registradas son un historial inmutable; no se pueden modificar ni eliminar.
CREATE OR REPLACE FUNCTION public.fn_protect_alerts() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'Las alertas registradas no se pueden %: el historial es inmutable',
        CASE TG_OP WHEN 'UPDATE' THEN 'modificar' ELSE 'eliminar' END
        USING ERRCODE = 'P0001';
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_protect_alerts ON public.alerts;
CREATE TRIGGER trg_protect_alerts BEFORE UPDATE OR DELETE ON public.alerts
    FOR EACH ROW EXECUTE FUNCTION public.fn_protect_alerts();
