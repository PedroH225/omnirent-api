package br.com.omnirent.item;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.ImageErrorType;
import br.com.omnirent.infrastructure.CompressedFile;
import br.com.omnirent.infrastructure.StorageService;
import br.com.omnirent.infrastructure.StorageUploadResponse;
import br.com.omnirent.item.domain.ItemImage;
import br.com.omnirent.item.domain.ItemImageRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@Service
@RequiredArgsConstructor
public class ItemImageService {

	private static final int MAX_IMAGES_PER_ITEM = 5;

    private final ItemImageRepository imageRepository;
    
    private final StorageService storageService;
        
    private static final Set<String> SUPPORTED_FORMATS =
            Set.of("jpeg", "png", "webp");
    
    private static final String SUPPORTED_FORMATS_MESSAGE =
            "JPEG, PNG, WebP";
    
    @Transactional
    public void saveImages(
            List<ItemImageRequestDto> imageRequests,
            Map<String, MultipartFile> files,
            String itemId) throws IOException {
    	
        imageRequests = Optional.ofNullable(imageRequests)
                .orElse(Collections.emptyList());

        files = Optional.ofNullable(files)
                .orElse(Collections.emptyMap());
        
       validateImageCount(imageRequests, files);
       validateDisplayOrders(imageRequests);
        
        LinkedHashMap<String, CompressedFile> compressedFiles = new LinkedHashMap<>();

        for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
            String id = entry.getKey();
            MultipartFile file = entry.getValue();

            CompressedFile compressed = compressImage(file);

            compressedFiles.put(id, compressed);
        }

        List<ItemImage> existingImages = imageRepository.findByItemId(itemId);

        deleteRemovedImages(existingImages, imageRequests);

        List<ItemImage> images = new ArrayList<>();

        images.addAll(updateExistingImages(existingImages, imageRequests));

        images.addAll(createNewImages(imageRequests, compressedFiles, itemId));

        validateImageLimit(images.size(), 0);

        imageRepository.saveAll(images);
    }


    private void deleteRemovedImages(
            List<ItemImage> existingImages,
            List<ItemImageRequestDto> imageRequests) {

        Set<UUID> imageIds = imageRequests.stream()
                .map(ItemImageRequestDto::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<ItemImage> imagesToDelete = existingImages.stream()
                .filter(image -> !imageIds.contains(image.getId()))
                .toList();

        deleteImages(imagesToDelete);
    }


    private List<ItemImage> updateExistingImages(
            List<ItemImage> existingImages,
            List<ItemImageRequestDto> imageRequests) {

        Map<UUID, Integer> orderMap = imageRequests.stream()
                .filter(request -> request.id() != null)
                .collect(Collectors.toMap(
                        ItemImageRequestDto::id,
                        ItemImageRequestDto::order
                ));

        return existingImages.stream()
                .filter(image -> orderMap.containsKey(image.getId()))
                .peek(image -> image.setDisplayOrder(
                        orderMap.get(image.getId())))
                .toList();
    }


    private List<ItemImage> createNewImages(
            List<ItemImageRequestDto> imageRequests,
            LinkedHashMap<String, CompressedFile> files,
            String itemId) {

        String path = String.format("items/%s", itemId);

        return imageRequests.stream()
                .filter(request -> request.id() == null)
                .map(request -> createImage(
                        request, files,
                        path, itemId))
                .toList();
    }


    private ItemImage createImage(
            ItemImageRequestDto request,
            Map<String, CompressedFile> files,
            String path,
            String itemId) {

        CompressedFile file = files.get(request.tempId());

        if (file == null) {
            throw new IllegalArgumentException(
                    "Missing file for tempId " + request.tempId());
        }

        StorageUploadResponse response =
                storageService.upload(file, path);

        return new ItemImage(
                response.id(), response.key(), request.order(),
                null, null, itemId);
    }


    private void deleteImages(List<ItemImage> images) {

        images.forEach(image ->
                storageService.delete(image.getStorageKey()));

        imageRepository.deleteAll(images);
    }
    
    public CompressedFile compressImage(MultipartFile file) throws IOException {
        BufferedImage bufferedImage = getValidBufferedImage(file);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails
                .of(bufferedImage)
                .size(1920, 1920)
                .outputFormat("webp")
                .outputQuality(0.8)
                .toOutputStream(outputStream);

        return new CompressedFile(
                outputStream.toByteArray(),
                "image/webp");
    }
    
    private void validateImageCount(List<ItemImageRequestDto> imageRequests,
            Map<String, MultipartFile> files) {

    	 if (imageRequests.size() > MAX_IMAGES_PER_ITEM ||
    	            files.size() > MAX_IMAGES_PER_ITEM) {
    	        throw new ApiException(
    	                ImageErrorType.MAX_IMAGES_EXCEEDED, MAX_IMAGES_PER_ITEM);
    	    }
    }
    
    private void validateImageLimit(int currentImages, int newImages) {

        if (currentImages + newImages > MAX_IMAGES_PER_ITEM) {
            throw new ApiException(ImageErrorType.MAX_IMAGES_EXCEEDED, MAX_IMAGES_PER_ITEM);
        }
    }	    
    
    public BufferedImage getValidBufferedImage(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
    	if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

    	try (ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream())) {
    	    Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

    	    if (!readers.hasNext()) {
    	        throw new ApiException(ImageErrorType.INVALID_IMAGE, filename);
    	    }

    	    ImageReader reader = readers.next();

    	    try {
    	        reader.setInput(input);

    	        String format = reader.getFormatName();

    	        if (!SUPPORTED_FORMATS.contains(format.toLowerCase(Locale.ROOT))) {
    	            throw new ApiException(
    	                ImageErrorType.UNSUPPORTED_MEDIA_TYPE,
    	                filename,
    	                SUPPORTED_FORMATS_MESSAGE
    	            );
    	        }

    	        BufferedImage bufferedImage = reader.read(0);

    	        if (bufferedImage == null) {
    	            throw new ApiException(ImageErrorType.INVALID_IMAGE, filename);
    	        }

    	        return bufferedImage;
    	    } finally {
    	        reader.dispose();
    	    }
    	}
    }
    
    private void validateDisplayOrders(List<ItemImageRequestDto> imageRequests) {
        long distinctOrders = imageRequests.stream()
                .map(ItemImageRequestDto::order)
                .distinct()
                .count();

        if (distinctOrders != imageRequests.size()) {
            throw new ApiException(ImageErrorType.DUPLICATE_IMAGE_ORDER);
        }
    }
}