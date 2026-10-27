package com.wakedrive.backend.device.service;

import com.wakedrive.backend.device.dto.DeviceConnectionRequestDTO;

public interface DeviceConnectionService {

    /** El dispositivo sigue conectado: actualiza su última conexión y resuelve sus alertas en espera. */
    void heartbeat(String deviceKey, DeviceConnectionRequestDTO request);

    /** El dispositivo avisa que se va a apagar: crea una alerta DEVICE_OFF en espera. */
    void shutdown(String deviceKey, DeviceConnectionRequestDTO request);

    /** Crea una alerta CONNECTION_LOST en espera para cada dispositivo que dejó de reportarse. */
    void detectLostConnections();
}
