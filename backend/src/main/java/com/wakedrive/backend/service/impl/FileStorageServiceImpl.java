package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.StoredFileDTO;
import com.wakedrive.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    @Value("${app.uploads.base-url}")
    private String baseUrl;

    @Override
    public StoredFileDTO store(MultipartFile file, String subFolder) {
        try {
            Path folder = Path.of(uploadsDir, subFolder);
            Files.createDirectories(folder);

            String extension = "";
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
            }
            String storedName = UUID.randomUUID() + extension;
            Path destination = folder.resolve(storedName);
            file.transferTo(destination);

            String relativePath = subFolder + "/" + storedName;
            return StoredFileDTO.builder()
                    .name(originalName)
                    .size(file.getSize())
                    .url(baseUrl + "/" + relativePath)
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
            Files.deleteIfExists(Path.of(uploadsDir, path));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete file", e);
        }
    }
}
