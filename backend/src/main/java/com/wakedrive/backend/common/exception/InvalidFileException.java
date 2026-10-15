package com.wakedrive.backend.common.exception;

import lombok.Getter;

@Getter
public class InvalidFileException extends RuntimeException {

    public static final String TOO_LARGE = "FILE_TOO_LARGE";
    public static final String INVALID_FORMAT = "FILE_INVALID_FORMAT";
    public static final String INVALID_PATH = "FILE_INVALID_PATH";

    private final String code;
    /** Campo del formulario al que pertenece el archivo (p. ej. "logo", "alarmSound"). */
    private final String field;

    public InvalidFileException(String code, String field, String message) {
        super(message);
        this.code = code;
        this.field = field;
    }
}
