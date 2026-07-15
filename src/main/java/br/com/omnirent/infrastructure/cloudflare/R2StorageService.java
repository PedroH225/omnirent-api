package br.com.omnirent.infrastructure.cloudflare;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.omnirent.exception.infrastructure.FileUploadException;
import br.com.omnirent.infrastructure.CompressedFile;
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
	public StorageUploadResponse upload(CompressedFile file, String path) {
		UUID uuid = UUID.randomUUID();
		String key = path + "/" + uuid + ".webp";

		PutObjectRequest request = PutObjectRequest.builder()
				.bucket(properties.bucket())
				.key(key)
				.contentType(file.contentType())
				.build();

		s3Client.putObject(request, RequestBody.fromBytes(file.bytes()));

		return new StorageUploadResponse(uuid, key);
	}

	@Override
	public void delete(String key) {
		DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(properties.bucket()).key(key).build();

		s3Client.deleteObject(request);
	}

	@Override
	public String getPublicUrl(String key) {
		return properties.endpoint() + "/" + key;
	}
}