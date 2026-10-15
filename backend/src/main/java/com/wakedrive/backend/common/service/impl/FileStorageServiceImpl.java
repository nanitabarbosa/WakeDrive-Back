package com.wakedrive.backend.common.service.impl;

import com.wakedrive.backend.common.dto.StoredFileDTO;
import com.wakedrive.backend.common.exception.InvalidFileException;
import com.wakedrive.backend.common.service.FileStorageService;
import com.wakedrive.backend.common.service.UploadKind;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final int SIGNATURE_BYTES = 12;

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    @Value("${app.uploads.base-url}")
    private String baseUrl;

    @Override
    public StoredFileDTO store(MultipartFile file, String subFolder, UploadKind kind) {
        if (file.getSize() > kind.getMaxBytes()) {
            throw new InvalidFileException(InvalidFileException.TOO_LARGE, kind.getFormField(),
                    "El archivo supera el tamaño máximo de " + kind.getMaxLabel());
        }

        try {
            String extension = detectExtension(file, kind);

            Path root = Path.of(uploadsDir).toAbsolutePath().normalize();
            Path folder = root.resolve(subFolder).normalize();
            if (!folder.startsWith(root)) {
                throw new InvalidFileException(InvalidFileException.INVALID_PATH, kind.getFormField(),
                        "La carpeta de destino del archivo no es válida");
            }
            Files.createDirectories(folder);

            String storedName = UUID.randomUUID() + extension;
            file.transferTo(folder.resolve(storedName));

            return StoredFileDTO.builder()
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .url(baseUrl + "/" + subFolder + "/" + storedName)
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String path) {
        if (path == null) {
            return;
        }
        try {
            Path root = Path.of(uploadsDir).toAbsolutePath().normalize();
            Path target = root.resolve(path).normalize();
            if (target.startsWith(root)) {
                Files.deleteIfExists(target);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete file", e);
        }
    }

    private String detectExtension(MultipartFile file, UploadKind kind) throws IOException {
        byte[] head;
        try (InputStream in = file.getInputStream()) {
            head = in.readNBytes(SIGNATURE_BYTES);
        }

        String extension = switch (kind) {
            case IMAGE -> isPng(head) ? ".png" : isJpeg(head) ? ".jpg" : null;
            case AUDIO -> isMp3(head) ? ".mp3" : null;
        };

        if (extension == null) {
            throw new InvalidFileException(InvalidFileException.INVALID_FORMAT, kind.getFormField(),
                    "Formato no permitido. Solo se aceptan archivos " + kind.getFormatsLabel());
        }
        return extension;
    }

    private boolean isPng(byte[] head) {
        return head.length >= 8
                && (head[0] & 0xFF) == 0x89 && head[1] == 'P' && head[2] == 'N' && head[3] == 'G'
                && head[4] == 0x0D && head[5] == 0x0A && head[6] == 0x1A && head[7] == 0x0A;
    }

    private boolean isJpeg(byte[] head) {
        return head.length >= 3
                && (head[0] & 0xFF) == 0xFF && (head[1] & 0xFF) == 0xD8 && (head[2] & 0xFF) == 0xFF;
    }

    private boolean isMp3(byte[] head) {
        if (head.length < 3) {
            return false;
        }
        boolean id3Tag = head[0] == 'I' && head[1] == 'D' && head[2] == '3';
        boolean frameSync = (head[0] & 0xFF) == 0xFF && (head[1] & 0xE0) == 0xE0;
        return id3Tag || frameSync;
    }
}
