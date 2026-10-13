package com.wakedrive.backend.common.service;

import lombok.Getter;

@Getter
public enum UploadKind {

    IMAGE(2L * 1024 * 1024, "2 MB", "PNG o JPG"),
    AUDIO(5L * 1024 * 1024, "5 MB", "MP3");

    private final long maxBytes;
    private final String maxLabel;
    private final String formatsLabel;

    UploadKind(long maxBytes, String maxLabel, String formatsLabel) {
        this.maxBytes = maxBytes;
        this.maxLabel = maxLabel;
        this.formatsLabel = formatsLabel;
    }
}
