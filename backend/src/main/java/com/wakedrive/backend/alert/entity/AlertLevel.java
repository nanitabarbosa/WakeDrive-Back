package com.wakedrive.backend.alert.entity;

public enum AlertLevel {
    HIGH,
    MEDIUM,
    LOW,
    /** Dispositivo apagado o sin señal: el nivel se calcula cuando vuelve a conectarse. */
    PENDING
}
