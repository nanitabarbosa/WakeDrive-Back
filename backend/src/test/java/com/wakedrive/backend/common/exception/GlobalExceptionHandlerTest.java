package com.wakedrive.backend.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wakedrive.backend.common.dto.ApiErrorDTO;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("PUT", "/api/settings");
    }

    @Test
    void unexpectedErrorReturns500WithErrorIdAndHidesInternalsInProd() {
        ReflectionTestUtils.setField(handler, "exposeDetails", false);

        ResponseEntity<ApiErrorDTO> response = handler.handleUnexpected(new IllegalStateException("secreto interno"), request);

        ApiErrorDTO body = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("/api/settings", body.getPath());
        assertEquals("Error interno del servidor", body.getMessage());
        assertEquals(1, body.getDetails().size());
        assertTrue(body.getDetails().get(0).startsWith("Código de error: "));
        assertTrue(!body.getDetails().toString().contains("secreto interno"));
    }

    @Test
    void unexpectedErrorShowsExceptionAndCauseInLocal() {
        ReflectionTestUtils.setField(handler, "exposeDetails", true);
        Exception error = new RuntimeException("falló algo", new NullPointerException("campo vacío"));

        ApiErrorDTO body = handler.handleUnexpected(error, request).getBody();

        assertTrue(body.getDetails().stream().anyMatch(d -> d.contains("RuntimeException: falló algo")));
        assertTrue(body.getDetails().stream().anyMatch(d -> d.contains("NullPointerException: campo vacío")));
    }

    @Test
    void fileIoErrorExplainsItIsAFileProblem() {
        ReflectionTestUtils.setField(handler, "exposeDetails", false);

        ApiErrorDTO body = handler.handleFileIo(new UncheckedIOException(new IOException("disco lleno")), request).getBody();

        assertEquals(500, body.getStatus());
        assertEquals("No se pudo leer o guardar el archivo en el servidor", body.getMessage());
    }

    @Test
    void uploadTooLargeReturns413() {
        ResponseEntity<Object> response = handler.handleExceptionInternal(
                new MaxUploadSizeExceededException(5 * 1024 * 1024), null, new HttpHeaders(),
                HttpStatus.PAYLOAD_TOO_LARGE, new ServletWebRequest(request));

        ApiErrorDTO body = (ApiErrorDTO) response.getBody();
        assertEquals(413, response.getStatusCode().value());
        assertEquals("El archivo supera el tamaño máximo permitido", body.getMessage());
        assertEquals("/api/settings", body.getPath());
    }

    @Test
    void methodNotAllowedNamesTheMethod() {
        ReflectionTestUtils.setField(handler, "exposeDetails", false);

        ResponseEntity<Object> response = handler.handleExceptionInternal(
                new HttpRequestMethodNotSupportedException("DELETE"), null, new HttpHeaders(),
                HttpStatus.METHOD_NOT_ALLOWED, new ServletWebRequest(request));

        ApiErrorDTO body = (ApiErrorDTO) response.getBody();
        assertEquals(405, body.getStatus());
        assertEquals("Método DELETE no permitido para esta ruta", body.getMessage());
        assertNull(body.getDetails());
        assertNotNull(body.getTimestamp());
    }
}
