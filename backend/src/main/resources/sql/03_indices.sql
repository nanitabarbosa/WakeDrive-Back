-- Índices de la tabla alerts
-- Idempotente: se puede ejecutar más de una vez sin error.

-- Índices de consulta sobre alerts (el dashboard filtra por rango de fechas, conductor y vehículo).
CREATE INDEX IF NOT EXISTS idx_alerts_detected_at ON public.alerts (detected_at);
CREATE INDEX IF NOT EXISTS idx_alerts_driver ON public.alerts (driver_id);
CREATE INDEX IF NOT EXISTS idx_alerts_vehicle ON public.alerts (vehicle_id);
