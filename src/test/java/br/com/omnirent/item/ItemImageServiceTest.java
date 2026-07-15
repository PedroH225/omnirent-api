package br.com.omnirent.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
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
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.infrastructure.StorageService;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemImageRequestDto;
import br.com.omnirent.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class ItemImageServiceTest {

	@InjectMocks
	private ItemImageService imageService;

	@Mock
    private ItemImageRepository imageRepository;
    
	@Mock
    private StorageService storageService;
	
	@Autowired
	private Clock clock;
	
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

        verify(imageRepository).saveAll(any());
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
        assertEquals(ImageErrorType.MAX_IMAGES_EXCEEDED.getErrorCode(),
        		exception.getErrorCode());
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
        assertEquals(ImageErrorType.MAX_IMAGES_EXCEEDED.getErrorCode(), exception.getErrorCode());
    }
	
	
}


