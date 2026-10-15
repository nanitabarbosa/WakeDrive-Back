package com.wakedrive.backend.common.service;

import lombok.Getter;

@Getter
public enum UploadKind {

    IMAGE("logo", 5L * 1024 * 1024, "5 MB", "PNG o JPG"),
    AUDIO("alarmSound", 5L * 1024 * 1024, "5 MB", "MP3");

    private final String formField;
    private final long maxBytes;
    private final String maxLabel;
    private final String formatsLabel;

    UploadKind(String formField, long maxBytes, String maxLabel, String formatsLabel) {
        this.formField = formField;
        this.maxBytes = maxBytes;
        this.maxLabel = maxLabel;
        this.formatsLabel = formatsLabel;
    }
}
