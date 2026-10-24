package com.wakedrive.backend.common.exception;

import com.wakedrive.backend.common.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Convierte toda excepción en un {@link ApiErrorDTO} con la misma forma:
 * {@code code} (estable, para el front), {@code message} (en español), {@code fieldErrors} (por campo) y {@code details} (técnico).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** Nombre en español de cada campo, para que el mensaje diga exactamente qué falla. */
    private static final Map<String, String> FIELD_LABELS = Map.ofEntries(
            Map.entry("companyName", "Nombre de la empresa"),
            Map.entry("nit", "NIT"),
            Map.entry("cityId", "Ciudad"),
            Map.entry("address", "Dirección"),
            Map.entry("companyPhone", "Teléfono de la empresa"),
            Map.entry("adminName", "Nombre del administrador"),
            Map.entry("adminEmail", "Correo del administrador"),
            Map.entry("adminPhone", "Teléfono del administrador"),
            Map.entry("phone", "Teléfono"),
            Map.entry("email", "Correo"),
            Map.entry("name", "Nombre"),
            Map.entry("fullName", "Nombre completo"),
            Map.entry("document", "Documento"),
            Map.entry("plate", "Placa"),
            Map.entry("brandId", "Marca"),
            Map.entry("model", "Modelo"),
            Map.entry("year", "Año"),
            Map.entry("vehicleTypeId", "Tipo de vehículo"),
            Map.entry("status", "Estado"),
            Map.entry("serial", "Serial"),
            Map.entry("userId", "Conductor"),
            Map.entry("vehicleId", "Vehículo"),
            Map.entry("deviceSerial", "Serial del dispositivo"),
            Map.entry("alarmDurationSeconds", "Duración de la alarma"),
            Map.entry("faceRecognitionAlways", "Reconocimiento de rostro"),
            Map.entry("notifyDeviceShutdown", "Notificación de apagado"),
            Map.entry("password", "Contraseña"),
            Map.entry("newPassword", "Nueva contraseña"),
            Map.entry("currentPassword", "Contraseña actual"),
            Map.entry("token", "Token"),
            Map.entry("refreshToken", "Sesión"),
            Map.entry("logo", "Logo"),
            Map.entry("alarmSound", "Sonido de la alarma"));

    /** Nombre en español de las tablas, para explicar qué registros usan el dato que se intenta borrar. */
    private static final Map<String, String> TABLE_LABELS = Map.of(
            "vinculations", "vinculaciones",
            "vehicles", "vehículos",
            "drivers", "conductores",
            "devices", "dispositivos",
            "alerts", "alertas",
            "notifications", "notificaciones",
            "users", "usuarios",
            "companies", "empresas");

    private static final Pattern UNIQUE_VIOLATION = Pattern.compile("Key \\((.+?)\\)=\\((.*?)\\) already exists");
    private static final Pattern FOREIGN_KEY_VIOLATION = Pattern.compile("is still referenced from table \"(.+?)\"");
    private static final Pattern NOT_NULL_VIOLATION = Pattern.compile("null value in column \"(.+?)\"");

    /** En local devuelve también la excepción y su causa para depurar; en producción se desactiva. */
    @Value("${app.errors.expose-details:false}")
    private boolean exposeDetails;

    // ------------------------------------------------------------------ errores de negocio

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), null, null, request.getRequestURI());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorDTO> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = ex.getField() != null ? Map.of(ex.getField(), ex.getMessage()) : null;
        return build(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", ex.getMessage(), fieldErrors, null, request.getRequestURI());
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidState(InvalidStateException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "INVALID_STATE", ex.getMessage(), null, null, request.getRequestURI());
    }

    @ExceptionHandler(TooManyAttemptsException.class)
    public ResponseEntity<ApiErrorDTO> handleTooManyAttempts(TooManyAttemptsException ex, HttpServletRequest request) {
        return build(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_ATTEMPTS", ex.getMessage(), null, null, request.getRequestURI());
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidFile(InvalidFileException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getCode(), ex.getMessage(),
                Map.of(ex.getField(), ex.getMessage()), null, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), null, null, request.getRequestURI());
    }

    // ------------------------------------------------------------------ seguridad

    /** Se lanza solo después de validar la contraseña, para no revelar a quien no la conoce que la empresa está inactiva. */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErrorDTO> handleDisabled(DisabledException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "COMPANY_INACTIVE",
                "La empresa está inactiva. Comunícate con el administrador de WakeDrive", null, null, request.getRequestURI());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDTO> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Correo o contraseña incorrectos", null, null, request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "No tienes permiso para realizar esta acción", null, null, request.getRequestURI());
    }

    // ------------------------------------------------------------------ base de datos y archivos

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation on {}: {}", request.getRequestURI(), rootMessage(ex));
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY", describeIntegrity(rootMessage(ex)),
                null, debug(ex), request.getRequestURI());
    }

    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<ApiErrorDTO> handleFileIo(UncheckedIOException ex, HttpServletRequest request) {
        String errorId = shortId();
        log.error("File error [{}] on {}", errorId, request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_IO_ERROR",
                "No se pudo leer o guardar el archivo en el servidor", null, withErrorId(errorId, ex), request.getRequestURI());
    }

    // ------------------------------------------------------------------ cualquier otro error inesperado

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleUnexpected(Exception ex, HttpServletRequest request) {
        String errorId = shortId();
        log.error("Unexpected error [{}] on {} {}", errorId, request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Error interno del servidor", null, withErrorId(errorId, ex), request.getRequestURI());
    }

    // ------------------------------------------------------------------ errores estándar de Spring MVC

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
        List<String> details = fieldErrors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList();
        String message = "Datos inválidos. " + fieldErrors.entrySet().stream()
                .map(entry -> labelOf(entry.getKey()) + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
        return respond(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message, fieldErrors, details, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        return respond(status, codeOf(ex, status), describe(ex, status), null, debug(ex), request);
    }

    private String codeOf(Exception ex, HttpStatus status) {
        if (ex instanceof HttpMessageNotReadableException) {
            return "MALFORMED_REQUEST";
        }
        if (ex instanceof MissingServletRequestPartException) {
            return "MISSING_PART";
        }
        if (ex instanceof MissingServletRequestParameterException) {
            return "MISSING_PARAMETER";
        }
        if (ex instanceof TypeMismatchException) {
            return "INVALID_PARAMETER";
        }
        if (ex instanceof MaxUploadSizeExceededException) {
            return InvalidFileException.TOO_LARGE;
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return "METHOD_NOT_ALLOWED";
        }
        if (ex instanceof HttpMediaTypeNotSupportedException) {
            return "UNSUPPORTED_MEDIA_TYPE";
        }
        if (ex instanceof NoResourceFoundException) {
            return "ROUTE_NOT_FOUND";
        }
        return status.name();
    }

    private String describe(Exception ex, HttpStatus status) {
        if (ex instanceof HttpMessageNotReadableException) {
            return "El cuerpo de la petición es inválido o tiene un formato incorrecto";
        }
        if (ex instanceof MissingServletRequestPartException part) {
            return "Falta la parte requerida de la petición: " + part.getRequestPartName();
        }
        if (ex instanceof MissingServletRequestParameterException param) {
            return "Falta el parámetro requerido: " + param.getParameterName();
        }
        if (ex instanceof TypeMismatchException mismatch) {
            return "El parámetro '" + mismatch.getPropertyName() + "' tiene un valor inválido: " + mismatch.getValue();
        }
        if (ex instanceof MaxUploadSizeExceededException) {
            return "El archivo supera el tamaño máximo permitido";
        }
        if (ex instanceof HttpRequestMethodNotSupportedException method) {
            return "Método " + method.getMethod() + " no permitido para esta ruta";
        }
        if (ex instanceof HttpMediaTypeNotSupportedException media) {
            return "Tipo de contenido no soportado: " + media.getContentType();
        }
        if (ex instanceof NoResourceFoundException) {
            return "Recurso no encontrado";
        }
        return status.getReasonPhrase();
    }

    // ------------------------------------------------------------------ utilidades

    private ResponseEntity<Object> respond(HttpStatus status, String code, String message,
                                           Map<String, String> fieldErrors, List<String> details, WebRequest request) {
        String path = request instanceof ServletWebRequest servletRequest ? servletRequest.getRequest().getRequestURI() : null;
        return ResponseEntity.status(status).body(body(status, code, message, fieldErrors, details, path));
    }

    private ResponseEntity<ApiErrorDTO> build(HttpStatus status, String code, String message,
                                              Map<String, String> fieldErrors, List<String> details, String path) {
        return ResponseEntity.status(status).body(body(status, code, message, fieldErrors, details, path));
    }

    private ApiErrorDTO body(HttpStatus status, String code, String message,
                             Map<String, String> fieldErrors, List<String> details, String path) {
        if (status.is4xxClientError()) {
            log.warn("{} {} on {}: {} {}", status.value(), code, path, message, fieldErrors != null ? fieldErrors : "");
        }
        return ApiErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .fieldErrors(fieldErrors)
                .details(details)
                .path(path)
                .build();
    }

    /** Detalle técnico (excepción y causa raíz), solo si app.errors.expose-details=true. */
    private List<String> debug(Throwable ex) {
        if (!exposeDetails) {
            return null;
        }
        List<String> details = new ArrayList<>();
        details.add(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        String root = rootMessage(ex);
        if (root != null && !root.equals(ex.getMessage())) {
            details.add("Causa: " + root);
        }
        return details;
    }

    private List<String> withErrorId(String errorId, Throwable ex) {
        List<String> details = new ArrayList<>();
        details.add("Código de error: " + errorId + " (búscalo en el log del servidor)");
        List<String> debug = debug(ex);
        if (debug != null) {
            details.addAll(debug);
        }
        return details;
    }

    private String rootMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root.getClass().getSimpleName() + ": " + root.getMessage();
    }

    private String labelOf(String field) {
        return FIELD_LABELS.getOrDefault(field, field);
    }

    /** Traduce el mensaje técnico de PostgreSQL a algo que el usuario entienda. */
    private String describeIntegrity(String rootMessage) {
        if (rootMessage != null) {
            Matcher unique = UNIQUE_VIOLATION.matcher(rootMessage);
            if (unique.find()) {
                return "Ya existe un registro con " + columnLabel(unique.group(1)) + " = " + unique.group(2);
            }
            Matcher foreignKey = FOREIGN_KEY_VIOLATION.matcher(rootMessage);
            if (foreignKey.find()) {
                String table = foreignKey.group(1);
                return "No se puede eliminar porque este registro está siendo usado en "
                        + TABLE_LABELS.getOrDefault(table, table);
            }
            Matcher notNull = NOT_NULL_VIOLATION.matcher(rootMessage);
            if (notNull.find()) {
                return columnLabel(notNull.group(1)) + ": Es obligatorio";
            }
        }
        return "No se pudo guardar: el dato está duplicado o está siendo usado por otro registro";
    }

    /** Convierte una columna (admin_email) en la etiqueta del campo (Correo del administrador). */
    private String columnLabel(String column) {
        StringBuilder camel = new StringBuilder();
        boolean upper = false;
        for (char c : column.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                camel.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return labelOf(camel.toString());
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
