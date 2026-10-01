package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.StoredFileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFileDTO store(MultipartFile file, String subFolder);

    void delete(String path);
}
