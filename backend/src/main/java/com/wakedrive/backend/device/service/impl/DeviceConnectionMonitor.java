package com.wakedrive.backend.device.service.impl;

import com.wakedrive.backend.device.service.DeviceConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Revisa cada cierto tiempo qué dispositivos dejaron de reportarse. */
@Component
@RequiredArgsConstructor
public class DeviceConnectionMonitor {

    private final DeviceConnectionService deviceConnectionService;

    @Scheduled(fixedDelayString = "${app.device.check-interval-ms:30000}")
    public void checkConnections() {
        deviceConnectionService.detectLostConnections();
    }
}
