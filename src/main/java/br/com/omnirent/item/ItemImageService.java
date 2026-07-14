package br.com.omnirent.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.omnirent.infrastructure.StorageService;
import br.com.omnirent.infrastructure.StorageUploadResponse;
import br.com.omnirent.item.domain.ItemImage;
import br.com.omnirent.item.domain.ItemImageRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemImageService {

    private static final int MAX_IMAGES_PER_ITEM = 5;

    private final ItemImageRepository imageRepository;
    private final StorageService storageService;

    @Transactional
    public void saveImages(
            List<ItemImageRequestDto> imageRequests,
            Map<String, MultipartFile> files,
            String itemId) {

        imageRequests = Optional.ofNullable(imageRequests)
                .orElse(Collections.emptyList());

        files = Optional.ofNullable(files)
                .orElse(Collections.emptyMap());

        List<ItemImage> existingImages = imageRepository.findByItemId(itemId);

        deleteRemovedImages(existingImages, imageRequests);

        List<ItemImage> images = new ArrayList<>();

        images.addAll(updateExistingImages(existingImages, imageRequests));

        images.addAll(createNewImages(imageRequests, files, itemId));

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
            Map<String, MultipartFile> files,
            String itemId) {

        String path = String.format("items/%s", itemId);

        return imageRequests.stream()
                .filter(request -> request.id() == null)
                .map(request -> createImage(
                        request,
                        files,
                        path,
                        itemId))
                .toList();
    }


    private ItemImage createImage(
            ItemImageRequestDto request,
            Map<String, MultipartFile> files,
            String path,
            String itemId) {

        MultipartFile file = files.get(request.tempId());

        if (file == null) {
            throw new IllegalArgumentException(
                    "Missing file for tempId " + request.tempId());
        }

        StorageUploadResponse response =
                storageService.upload(file, path);

        return new ItemImage(
                response.id(),
                response.key(),
                request.order(),
                null,
                null,
                itemId);
    }


    private void deleteImages(List<ItemImage> images) {

        images.forEach(image ->
                storageService.delete(image.getStorageKey()));

        imageRepository.deleteAll(images);
    }


    private void validateImageLimit(int currentImages, int newImages) {

        if (currentImages + newImages > MAX_IMAGES_PER_ITEM) {
            throw new IllegalArgumentException(
                    "An item can have at most " + MAX_IMAGES_PER_ITEM + " images"
            );
        }
    }
}