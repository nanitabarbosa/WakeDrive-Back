package com.wakedrive.backend.common.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {

    /** Campo del formulario que está duplicado (p. ej. "plate", "nit"). */
    private final String field;

    public DuplicateResourceException(String field, String message) {
        super(message);
        this.field = field;
    }
}
