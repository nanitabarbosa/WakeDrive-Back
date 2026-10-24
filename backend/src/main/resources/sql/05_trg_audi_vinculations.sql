-- Trigger de auditoría de public.vinculations (guarda en public.audi_vinculations).
CREATE OR REPLACE FUNCTION public.fn_audi_vinculations() RETURNS trigger AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO public.audi_vinculations (operation, state, device_id, driver_id, linked_at, vehicle_id, vinculation_id)
        VALUES (TG_OP, 'NEW', NEW.device_id, NEW.driver_id, NEW.linked_at, NEW.vehicle_id, NEW.vinculation_id);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO public.audi_vinculations (operation, state, device_id, driver_id, linked_at, vehicle_id, vinculation_id)
        VALUES (TG_OP, 'OLD', OLD.device_id, OLD.driver_id, OLD.linked_at, OLD.vehicle_id, OLD.vinculation_id);
        INSERT INTO public.audi_vinculations (operation, state, device_id, driver_id, linked_at, vehicle_id, vinculation_id)
        VALUES (TG_OP, 'NEW', NEW.device_id, NEW.driver_id, NEW.linked_at, NEW.vehicle_id, NEW.vinculation_id);
        RETURN NEW;
    ELSE
        INSERT INTO public.audi_vinculations (operation, state, device_id, driver_id, linked_at, vehicle_id, vinculation_id)
        VALUES (TG_OP, 'OLD', OLD.device_id, OLD.driver_id, OLD.linked_at, OLD.vehicle_id, OLD.vinculation_id);
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_audi_vinculations ON public.vinculations;
CREATE TRIGGER trg_audi_vinculations AFTER INSERT OR UPDATE OR DELETE ON public.vinculations
    FOR EACH ROW EXECUTE FUNCTION public.fn_audi_vinculations();
