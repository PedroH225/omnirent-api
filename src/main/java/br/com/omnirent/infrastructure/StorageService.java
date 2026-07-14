package br.com.omnirent.infrastructure;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StorageUploadResponse upload(MultipartFile file, String path);

    void delete(String key);

    String getPublicUrl(String key);
}