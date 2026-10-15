package com.wakedrive.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    /** Identificador estable del error (p. ej. VALIDATION_FAILED); el front decide qué mostrar según este valor. */
    private String code;
    /** Mensaje legible para el usuario, en español. */
    private String message;
    /** Error por campo del formulario: { "nit": "mensaje" }. Solo en errores de validación o duplicados. */
    private Map<String, String> fieldErrors;
    /** Detalle técnico (solo si app.errors.expose-details=true). */
    private List<String> details;
    private String path;
}
