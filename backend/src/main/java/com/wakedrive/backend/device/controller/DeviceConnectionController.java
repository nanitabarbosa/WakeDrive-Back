package com.wakedrive.backend.device.controller;

import com.wakedrive.backend.device.dto.DeviceConnectionRequestDTO;
import com.wakedrive.backend.device.service.DeviceConnectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Endpoints que llama el dispositivo (no un usuario): se autentica con el header X-Device-Key. */
@RestController
@RequestMapping("/api/device-connection")
@RequiredArgsConstructor
public class DeviceConnectionController {

    private final DeviceConnectionService deviceConnectionService;

    @PostMapping("/heartbeat")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void heartbeat(@RequestHeader(value = "X-Device-Key", required = false) String deviceKey,
                          @Valid @RequestBody DeviceConnectionRequestDTO request) {
        deviceConnectionService.heartbeat(deviceKey, request);
    }

    @PostMapping("/shutdown")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void shutdown(@RequestHeader(value = "X-Device-Key", required = false) String deviceKey,
                         @Valid @RequestBody DeviceConnectionRequestDTO request) {
        deviceConnectionService.shutdown(deviceKey, request);
    }
}
