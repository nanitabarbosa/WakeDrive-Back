package com.wakedrive.backend.common.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wakedrive.backend.common.dto.StoredFileDTO;
import com.wakedrive.backend.common.service.UploadKind;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

class FileStorageServiceImplTest {

    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0, 0, 0};
    private static final byte[] MP3 = {'I', 'D', '3', 3, 0, 0, 0, 0, 0, 0};

    @TempDir
    Path uploads;

    private FileStorageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FileStorageServiceImpl();
        ReflectionTestUtils.setField(service, "uploadsDir", uploads.toString());
        ReflectionTestUtils.setField(service, "baseUrl", "http://host/uploads");
    }

    @Test
    void storesPngAndIgnoresClientExtension() {
        MockMultipartFile file = new MockMultipartFile("logo", "evil.html", "text/html", PNG);

        StoredFileDTO stored = service.store(file, "logos", UploadKind.IMAGE);

        assertTrue(stored.getUrl().endsWith(".png"));
        String storedName = stored.getUrl().substring("http://host/uploads/logos/".length());
        assertTrue(Files.exists(uploads.resolve("logos").resolve(storedName)));
    }

    @Test
    void storesJpegAndMp3() {
        assertTrue(service.store(new MockMultipartFile("f", "a.jpg", "image/jpeg", JPEG), "logos", UploadKind.IMAGE)
                .getUrl().endsWith(".jpg"));
        assertTrue(service.store(new MockMultipartFile("f", "a.mp3", "audio/mpeg", MP3), "alarm-sounds", UploadKind.AUDIO)
                .getUrl().endsWith(".mp3"));
    }

    @Test
    void rejectsHtmlDisguisedAsImage() {
        byte[] html = "<html><script>alert(1)</script></html>".getBytes();
        MockMultipartFile file = new MockMultipartFile("logo", "logo.png", "image/png", html);

        assertThrows(IllegalArgumentException.class, () -> service.store(file, "logos", UploadKind.IMAGE));
    }

    @Test
    void rejectsImageOverTwoMegabytes() {
        byte[] big = new byte[2 * 1024 * 1024 + 1];
        System.arraycopy(PNG, 0, big, 0, PNG.length);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.store(new MockMultipartFile("logo", "logo.png", "image/png", big), "logos", UploadKind.IMAGE));
        assertEquals("El archivo supera el tamaño máximo de 2 MB", error.getMessage());
    }

    @Test
    void rejectsAudioOverFiveMegabytesButAcceptsExactLimit() {
        byte[] exact = new byte[5 * 1024 * 1024];
        System.arraycopy(MP3, 0, exact, 0, MP3.length);
        byte[] over = new byte[5 * 1024 * 1024 + 1];
        System.arraycopy(MP3, 0, over, 0, MP3.length);

        service.store(new MockMultipartFile("f", "a.mp3", "audio/mpeg", exact), "alarm-sounds", UploadKind.AUDIO);
        assertThrows(IllegalArgumentException.class,
                () -> service.store(new MockMultipartFile("f", "a.mp3", "audio/mpeg", over), "alarm-sounds", UploadKind.AUDIO));
    }

    @Test
    void deleteDoesNotLeaveUploadsFolder() throws Exception {
        Path outside = Files.createTempFile("outside", ".txt");
        try {
            service.delete("../" + outside.getFileName());
            assertTrue(Files.exists(outside));
        } finally {
            Files.deleteIfExists(outside);
        }
    }
}
