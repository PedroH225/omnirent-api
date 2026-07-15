package br.com.omnirent.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.ImageErrorType;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemImageTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.MultipartFileTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.infrastructure.CompressedFile;
import br.com.omnirent.infrastructure.StorageService;
import br.com.omnirent.infrastructure.StorageUploadResponse;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemImage;
import br.com.omnirent.item.domain.ItemImageRequestDto;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class ItemImageServiceTest {

	@InjectMocks
	private ItemImageService imageService;

	@Mock
    private ItemImageRepository imageRepository;
    
	@Mock
    private StorageService storageService;
	
	@Mock
    private CurrentUserProvider currentUserProvider;
    
	@Mock
    private ItemAuthorizationService authorizationService;
	
	private static final Instant FIXED_INSTANT =
	        Instant.parse("2026-01-01T00:00:00Z");
	
	@Captor
	private ArgumentCaptor<List<ItemImage>> itemImagesCaptor;
	
	private User owner;

	private Address ownerAddress;

	private Category tools;
	private SubCategory drill;

	private Item item;
	
	@BeforeEach
	void setUp() {
		owner = UserTestFactory.persistedOwner();

		ownerAddress = AddressTestFactory.forPersistedUser(owner);

        tools = CategoryTestFactory.createPersisted("Tools");
        drill = SubCategoryTestFactory.createPersisted("Drill", tools);

        item = ItemTestFactory.createPersisted(owner, ownerAddress, drill,
        		"200", ItemCondition.NEW);
	}
	
	@Test
    void saveImages_allowsMaximumNumberOfImages() throws Exception {
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 1),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 2),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 3),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 4),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 5)
        );

        imageService.saveImages(requests, Collections.emptyMap(), item.getId());

        verify(imageRepository, never()).saveAll(any());
    }
	
	@Test
    void saveImages_throwsWhenImageRequestLimitExceeded() {
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 1),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 2),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 3),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 4),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 5),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 6)
        );

        ApiException exception = assertThrows(ApiException.class, () ->
        imageService.saveImages(requests, Collections.emptyMap(), item.getId()));
        assertEquals(
        		ImageErrorType.MAX_IMAGES_EXCEEDED.getErrorCode(), exception.getErrorCode());
	}

    @Test
    void saveImages_throwsWhenFileLimitExceeded() {
        Map<String, MultipartFile> files = Map.of(
                "1", mock(MultipartFile.class),
                "2", mock(MultipartFile.class),
                "3", mock(MultipartFile.class),
                "4", mock(MultipartFile.class),
                "5", mock(MultipartFile.class),
                "6", mock(MultipartFile.class)
        );

        ApiException exception = assertThrows(ApiException.class, () ->
        imageService.saveImages(Collections.emptyList(), files, item.getId()));
        assertEquals(
        		ImageErrorType.MAX_IMAGES_EXCEEDED.getErrorCode(), exception.getErrorCode());
    }
	
    @Test
    void saveImages_savesImagesWhenDisplayOrdersAreUnique() throws Exception {
    	ItemImage image1 = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);
    	ItemImage image2 = ItemImageTestFactory.createPersisted(item, 2, FIXED_INSTANT);

    	when(imageRepository.findByItemId(item.getId()))
    	    .thenReturn(List.of(image1, image2));

    	List<ItemImageRequestDto> requests = List.of(
    	    ItemImageTestFactory.createRequest(image1.getId(), null, 1),
    	    ItemImageTestFactory.createRequest(image2.getId(), null, 2)
    	);

        imageService.saveImages(requests, Collections.emptyMap(), item.getId());

        verify(imageRepository).saveAll(any());
        verify(imageRepository).saveAll(itemImagesCaptor.capture());

        List<ItemImage> images = itemImagesCaptor.getValue();

        assertEquals(2, images.size());
        assertEquals(1, images.get(0).getDisplayOrder());
        assertEquals(2, images.get(1).getDisplayOrder());
    }

    @Test
    void saveImages_throwsDuplicateImageOrderWhenDisplayOrdersAreDuplicated() {
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 1),
                ItemImageTestFactory.createRequest(UUID.randomUUID(), null, 1)
        );

        ApiException exception = assertThrows(ApiException.class, () ->
                imageService.saveImages(requests, Collections.emptyMap(), item.getId()));
    
        assertEquals(
        		ImageErrorType.DUPLICATE_IMAGE_ORDER.getErrorCode(), exception.getErrorCode());
    }

    @Test
    void saveImages_createsNewImagesWhenTempIdsAreProvided() throws Exception {
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(null, "temp1", 1)
        );

        MultipartFile file = MultipartFileTestFactory.png();

		Map<String, MultipartFile> files = Map.of("temp1", file);
		
		when(imageRepository.findByItemId(item.getId()))
		        .thenReturn(Collections.emptyList());
		
		UUID imageId = UUID.randomUUID();
		
		when(storageService.upload(any(CompressedFile.class), eq("items/" + item.getId())))
		        .thenReturn(new StorageUploadResponse(imageId, "key1"));

        imageService.saveImages(requests, files, item.getId());

        verify(storageService).upload(any(CompressedFile.class), eq("items/" + item.getId()));
        verify(imageRepository).saveAll(itemImagesCaptor.capture());

        List<ItemImage> images = itemImagesCaptor.getValue();

        assertEquals(1, images.size());

        ItemImage image = images.getFirst();

        assertEquals(imageId, image.getId());
        assertEquals("key1", image.getStorageKey());
        assertEquals(1, image.getDisplayOrder());
        assertEquals(item.getId(), image.getItemId());
    }
    
    @Test
    void saveImages_updatesDisplayOrderOfExistingImages() throws Exception {
        ItemImage existingImage = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(existingImage.getId(), null, 5)
        );

        when(imageRepository.findByItemId(item.getId())).thenReturn(List.of(existingImage));

        imageService.saveImages(requests, Collections.emptyMap(), item.getId());

        verify(storageService, never()).upload(any(), anyString());
        verify(storageService, never()).delete(anyString());
        
        verify(imageRepository).saveAll(itemImagesCaptor.capture());

        List<ItemImage> images = itemImagesCaptor.getValue();

        assertEquals(1, images.size());
        assertEquals(existingImage.getId(), images.getFirst().getId());
        assertEquals(5, images.getFirst().getDisplayOrder()); 
    }
    
    
    @Test
    void saveImages_removesImagesMissingFromRequest() throws Exception {
        ItemImage existingImage = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);

        when(imageRepository.findByItemId(item.getId())).thenReturn(List.of(existingImage));

        imageService.saveImages(Collections.emptyList(), Collections.emptyMap(), item.getId());

        verify(imageRepository, never()).saveAll(any());
        verify(storageService).delete(existingImage.getStorageKey());

        verify(imageRepository).deleteAll(itemImagesCaptor.capture());

        List<ItemImage> deleted = itemImagesCaptor.getValue();

        assertEquals(1, deleted.size());
        assertEquals(existingImage, deleted.getFirst());   
    }
    
    @Test
    void saveImages_updatesAndCreatesImagesInTheSameRequest() throws Exception {
        ItemImage existingImage = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(existingImage.getId(), null, 2),
                ItemImageTestFactory.createRequest(null, "temp1", 1)
        );
        Map<String, MultipartFile> files = Map.of("temp1", MultipartFileTestFactory.jpeg());

        when(imageRepository.findByItemId(item.getId())).thenReturn(List.of(existingImage));
        when(storageService.upload(any(CompressedFile.class), anyString()))
                .thenReturn(new StorageUploadResponse(UUID.randomUUID(), "key1"));

        imageService.saveImages(requests, files, item.getId());

        verify(storageService, times(1)).upload(any(CompressedFile.class), anyString());
        verify(imageRepository).saveAll(itemImagesCaptor.capture());

        List<ItemImage> images = itemImagesCaptor.getValue();

        assertEquals(2, images.size());
        assertEquals(existingImage.getId(), images.get(0).getId());
        assertEquals(2, images.get(0).getDisplayOrder());
        assertEquals(1, images.get(1).getDisplayOrder());    
    }
    
    @Test
    void saveImages_removesAllImagesWhenImageRequestsAreNull() throws Exception {
        ItemImage image = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);

        when(imageRepository.findByItemId(item.getId()))
                .thenReturn(List.of(image));

        imageService.saveImages(null, Collections.emptyMap(), item.getId());

        verify(storageService, never()).upload(any(), anyString());
        verify(storageService).delete(image.getStorageKey());
        verify(imageRepository).deleteAll(List.of(image));
    }
    
    @Test
    void saveImages_updatesExistingImagesWhenFilesAreNull() throws Exception {
        ItemImage existingImage = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(existingImage.getId(), null, 2)
        );

        when(imageRepository.findByItemId(item.getId())).thenReturn(List.of(existingImage));

        imageService.saveImages(requests, null, item.getId());

        verify(imageRepository).saveAll(itemImagesCaptor.capture());

        List<ItemImage> images = itemImagesCaptor.getValue();

        verify(storageService, never()).upload(any(), anyString());
        assertEquals(1, images.size());
        assertEquals(existingImage.getId(), images.getFirst().getId());
        assertEquals(2, images.getFirst().getDisplayOrder());    
    }
    
    @Test
    void saveImages_throwsExceptionWhenTempIdHasNoMatchingFile() {
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(null, "missing", 1)
        );

        assertThrows(IllegalArgumentException.class, () ->
                imageService.saveImages(requests, Collections.emptyMap(), item.getId()));
       
        verify(storageService, never()).upload(any(), anyString());
        verify(imageRepository, never()).saveAll(any());
    }
    
    @Test
    void saveImages_savesAllCreatedAndUpdatedImages() throws Exception {
        ItemImage existingImage = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(existingImage.getId(), null, 2),
                ItemImageTestFactory.createRequest(null, "temp1", 1)
        );
        Map<String, MultipartFile> files = Map.of("temp1", MultipartFileTestFactory.png());

        when(imageRepository.findByItemId(item.getId())).thenReturn(List.of(existingImage));
        when(storageService.upload(any(CompressedFile.class), anyString()))
                .thenReturn(new StorageUploadResponse(UUID.randomUUID(), "key1"));

        imageService.saveImages(requests, files, item.getId());

        verify(imageRepository).saveAll(itemImagesCaptor.capture());

        List<ItemImage> images = itemImagesCaptor.getValue();

        assertEquals(2, images.size());
        assertTrue(images.stream()
                .anyMatch(image ->
                        image.getId().equals(existingImage.getId())
                        && image.getDisplayOrder() == 2));

        assertTrue(images.stream()
                .anyMatch(image ->
                        image.getStorageKey().equals("key1")
                        && image.getDisplayOrder() == 1));    
    }
    
    @Test
    void saveImages_uploadsOnlyNewImages() throws Exception {
        ItemImage existingImage = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(existingImage.getId(), null, 1),
                ItemImageTestFactory.createRequest(null, "temp1", 2)
        );
        Map<String, MultipartFile> files = Map.of("temp1", MultipartFileTestFactory.png());

        when(imageRepository.findByItemId(item.getId())).thenReturn(List.of(existingImage));
        when(storageService.upload(any(CompressedFile.class), anyString()))
                .thenReturn(new StorageUploadResponse(UUID.randomUUID(), "key1"));

        imageService.saveImages(requests, files, item.getId());
        
        verify(storageService).upload(
                any(CompressedFile.class),
                eq("items/" + item.getId())
        );
        verify(storageService).upload(any(CompressedFile.class), anyString());    
    }
    
    @Test
    void saveImages_deletesOnlyRemovedImages() throws Exception {
    	ItemImage keptImage =
    	        ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);
    	ItemImage removedImage =
    	        ItemImageTestFactory.createPersisted(item, 2, FIXED_INSTANT);
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(keptImage.getId(), null, 1)
        );

        when(imageRepository.findByItemId(item.getId())).thenReturn(List.of(keptImage, removedImage));

        imageService.saveImages(requests, Collections.emptyMap(), item.getId());

        verify(imageRepository).deleteAll(List.of(removedImage));
        verify(imageRepository).saveAll(any());
        verify(storageService, times(1)).delete(removedImage.getStorageKey());
        verify(storageService, never()).delete(keptImage.getStorageKey());
    }
    
    @Test
    void saveImages_deletesImagesMissingFromRequest() throws Exception {
        ItemImage removedImage = ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);

        when(imageRepository.findByItemId(item.getId())).thenReturn(List.of(removedImage));

        imageService.saveImages(Collections.emptyList(), Collections.emptyMap(), item.getId());
        
        verify(storageService).delete(removedImage.getStorageKey());
        verify(imageRepository).deleteAll(itemImagesCaptor.capture());

        List<ItemImage> deletedImages = itemImagesCaptor.getValue();

        assertEquals(1, deletedImages.size());
        assertEquals(removedImage.getId(), deletedImages.getFirst().getId());
    }
    
    @Test
    void saveImages_doesNotDeleteImagesStillPresent() throws Exception {
        ItemImage keptImage =
                ItemImageTestFactory.createPersisted(item, 1, FIXED_INSTANT);

        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(keptImage.getId(), null, 1)
        );

        when(imageRepository.findByItemId(item.getId()))
                .thenReturn(List.of(keptImage));

        imageService.saveImages(requests, Collections.emptyMap(), item.getId());
       
        verify(imageRepository, never()).deleteAll(any());
        verify(storageService, never()).delete(anyString());
    }
    
    @Test
    void saveImages_uploadsImagesToItemPath() throws Exception {
        List<ItemImageRequestDto> requests = List.of(
                ItemImageTestFactory.createRequest(null, "temp1", 1)
        );
        Map<String, MultipartFile> files = Map.of("temp1", MultipartFileTestFactory.png());

        when(storageService.upload(any(CompressedFile.class), anyString()))
                .thenReturn(new StorageUploadResponse(UUID.randomUUID(), "key1"));

        imageService.saveImages(requests, files, item.getId());

        verify(storageService).upload(any(CompressedFile.class), eq(String.format("items/%s", item.getId())));
    }
    
    @Test
    void getValidBufferedImage_returnsBufferedImageWhenImageIsValid() throws Exception {
        MultipartFile file = MultipartFileTestFactory.png();

        BufferedImage result = imageService.getValidBufferedImage(file);

        assertNotNull(result);
    }
    
    @Test
    void getValidBufferedImage_throwsExceptionWhenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);
        when(file.getOriginalFilename()).thenReturn("empty.png");

        assertThrows(IllegalArgumentException.class, () ->
                imageService.getValidBufferedImage(file));
    }
    
    @Test
    void getValidBufferedImage_throwsExceptionWhenImageIsCorrupted() {
        MultipartFile file = MultipartFileTestFactory.corruptedImage();

        ApiException exception = assertThrows(ApiException.class, () ->
                imageService.getValidBufferedImage(file));

        assertEquals(
                ImageErrorType.INVALID_IMAGE.getErrorCode(),
                exception.getErrorCode()
        );
    }
    
    @Test
    void getValidBufferedImage_throwsExceptionWhenFormatIsUnsupported() throws IOException {
        MultipartFile file = MultipartFileTestFactory.image("image/tiff", "tiff");

        ApiException exception = assertThrows(ApiException.class, () ->
                imageService.getValidBufferedImage(file));

        assertEquals(
                ImageErrorType.UNSUPPORTED_MEDIA_TYPE.getErrorCode(),
                exception.getErrorCode()
        );
    }
}


