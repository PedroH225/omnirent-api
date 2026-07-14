package br.com.omnirent.infrastructure.cloudflare;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.omnirent.exception.infrastructure.FileUploadException;
import br.com.omnirent.infrastructure.StorageService;
import br.com.omnirent.infrastructure.StorageUploadResponse;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class R2StorageService implements StorageService {

    private final S3Client s3Client;
    
    private final CloudflareProperties properties;

    @Override
    public StorageUploadResponse upload(MultipartFile file, String path) {
    	String filename = normalizeFilename(
    	        Optional.ofNullable(file.getOriginalFilename())
    	                .orElseThrow(() -> new IllegalArgumentException("Filename is missing"))
    	);

        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
        String key = path + "/" + uuid + "-" + filename;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
      
            try (InputStream input = file.getInputStream()) {
                s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(input, file.getSize())
                );
            }

            return new StorageUploadResponse(uuid, key);

        } catch (IOException e) {
            throw new FileUploadException(e);
        }
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    @Override
    public String getPublicUrl(String key) {
        return properties.endpoint() + "/" + key;
    }
    
    private String normalizeFilename(String filename) {
        String extension = "";

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = filename.substring(dotIndex).toLowerCase();
            filename = filename.substring(0, dotIndex);
        }

        String normalized = Normalizer.normalize(
                filename,
                Normalizer.Form.NFD
        );

        normalized = normalized
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]+", "-")
                .replaceAll("^-|-$", "")
                .toLowerCase();

        return normalized + extension;
    }
}