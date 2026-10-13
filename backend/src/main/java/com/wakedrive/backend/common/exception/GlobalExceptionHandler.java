package com.wakedrive.backend.common.exception;

import com.wakedrive.backend.common.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** En local devuelve también la excepción y su causa para depurar; en producción se desactiva. */
    @Value("${app.errors.expose-details:false}")
    private boolean exposeDetails;

    // ------------------------------------------------------------------ errores de negocio

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null, request.getRequestURI());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorDTO> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null, request.getRequestURI());
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidState(InvalidStateException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null, request.getRequestURI());
    }

    @ExceptionHandler(TooManyAttemptsException.class)
    public ResponseEntity<ApiErrorDTO> handleTooManyAttempts(TooManyAttemptsException ex, HttpServletRequest request) {
        return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), null, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null, request.getRequestURI());
    }

    // ------------------------------------------------------------------ seguridad

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDTO> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid email or password", null, request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción", null, request.getRequestURI());
    }

    // ------------------------------------------------------------------ base de datos y archivos

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation on {}: {}", request.getRequestURI(), rootMessage(ex));
        return build(HttpStatus.CONFLICT,
                "No se pudo guardar: el dato está duplicado o está siendo usado por otro registro",
                debug(ex), request.getRequestURI());
    }

    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<ApiErrorDTO> handleFileIo(UncheckedIOException ex, HttpServletRequest request) {
        String errorId = shortId();
        log.error("File error [{}] on {}", errorId, request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "No se pudo leer o guardar el archivo en el servidor", withErrorId(errorId, ex), request.getRequestURI());
    }

    // ------------------------------------------------------------------ cualquier otro error inesperado

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleUnexpected(Exception ex, HttpServletRequest request) {
        String errorId = shortId();
        log.error("Unexpected error [{}] on {} {}", errorId, request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor", withErrorId(errorId, ex), request.getRequestURI());
    }

    // ------------------------------------------------------------------ errores estándar de Spring MVC

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        return respond(HttpStatus.BAD_REQUEST, "Validation failed", details, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        return respond(status, describe(ex, status), debug(ex), request);
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

    private ResponseEntity<Object> respond(HttpStatus status, String message, List<String> details, WebRequest request) {
        String path = request instanceof ServletWebRequest servletRequest ? servletRequest.getRequest().getRequestURI() : null;
        return ResponseEntity.status(status).body(body(status, message, details, path));
    }

    private ResponseEntity<ApiErrorDTO> build(HttpStatus status, String message, List<String> details, String path) {
        return ResponseEntity.status(status).body(body(status, message, details, path));
    }

    private ApiErrorDTO body(HttpStatus status, String message, List<String> details, String path) {
        return ApiErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
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

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
