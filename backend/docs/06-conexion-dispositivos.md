# 06 — Conexión de dispositivos y alertas en espera

## Qué se creó o cambió

- **Nivel `PENDING` ("En espera"):** `AlertLevel` lo agrega. Lo usan las alertas `DEVICE_OFF` y `CONNECTION_LOST` mientras el dispositivo no vuelve a conectarse; al volver se guardan los segundos desconectado en `duration_seconds` y el nivel definitivo.
- **Endpoints del dispositivo** (`DeviceConnectionController`), con body `{"serial": "WD-0001-A1", "location": "opcional"}` y header `X-Device-Key`. Responden 204; 403 si la llave no coincide y 404 si el serial no existe.
  - `POST /api/device-connection/heartbeat`: "sigo vivo". Actualiza `devices.last_connection` y resuelve las alertas en espera del dispositivo.
  - `POST /api/device-connection/shutdown`: el dispositivo avisa que se apaga. Resuelve lo que tuviera en espera y crea una alerta `DEVICE_OFF` en espera.
- **Detección de señal perdida** (`DeviceConnectionMonitor`): cada 30 s busca dispositivos `ACTIVE` sin reportarse hace más de 2 min y les crea una alerta `CONNECTION_LOST` en espera, con `detected_at` = su última conexión. No repite si ya tiene una en espera.
- **Cálculo del nivel al reconectar** (`DeviceConnectionServiceImpl.calculateLevel`), contando las alertas del mismo conductor:

  | Tipo | LOW | MEDIUM | HIGH |
  |---|---|---|---|
  | `CONNECTION_LOST` | menos de 15 min | de 15 a 60 min, o 3 o más en el día | más de 60 min, o hasta 5 min después de una somnolencia o un rostro no detectado |
  | `DEVICE_OFF` | menos de 5 min (reinicio) o más de 4 h (fin de jornada) | de 5 min a 4 h | hasta 5 min después de una somnolencia o un rostro no detectado, o 3 o más en el día |

- **Base de datos** (`sql/00_esquema.sql`, `sql/02_triggers_negocio.sql` y `update.sql`):
  - El CHECK `alerts_level_check` acepta `PENDING`.
  - `fn_protect_alerts` sigue impidiendo modificar o eliminar alertas, salvo pasar una `PENDING` a su nivel definitivo cambiando solo `level` y `duration_seconds`.
- **Configuración** (`application.properties`): `app.device.api-key`, `app.device.offline-after-seconds` (120) y `app.device.check-interval-ms` (30000).

## Decisiones de diseño

- Un dispositivo sin señal no puede avisar que la perdió: es el backend quien lo detecta porque deja de llegar el "sigo vivo".
- `DEVICE_OFF` solo se crea si el dispositivo alcanza a avisar que se apaga. Si se desconecta de golpe (lo desenchufan), queda como `CONNECTION_LOST`.
- Los endpoints del dispositivo son públicos para Spring Security (el dispositivo no tiene usuario ni JWT); la llave se valida en el servicio.
- Sin vinculación no hay conductor ni vehículo a quien asignar la alerta, así que no se crea.
- La ubicación de la alerta es la que manda el dispositivo al apagarse; si no la manda, o se perdió la señal, se usa la de su última alerta.

## Supuestos y pendientes

- **Aplicar a una base existente:** correr `update.sql` (agrega `PENDING` al CHECK y actualiza el trigger).
- **Cambiar la llave en producción** con la variable `DEVICE_API_KEY`.
- Con los datos de ejemplo, ningún dispositivo se ha reportado después del 20 de octubre: al arrancar, el monitor crea una alerta `CONNECTION_LOST` en espera para cada dispositivo activo vinculado que no tenga una.
- El dispositivo físico debe llamar al "sigo vivo" con una frecuencia menor a `offline-after-seconds` (por ejemplo, cada 30 s).
