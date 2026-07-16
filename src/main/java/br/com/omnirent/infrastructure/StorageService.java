package br.com.omnirent.infrastructure;

public interface StorageService {

    StorageUploadResponse upload(CompressedFile file, String path);

    void delete(String key);

    String getPublicUrl(String key);
}