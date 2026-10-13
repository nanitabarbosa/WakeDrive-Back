package com.wakedrive.backend.common.service;

import com.wakedrive.backend.common.dto.StoredFileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFileDTO store(MultipartFile file, String subFolder, UploadKind kind);

    void delete(String path);
}
