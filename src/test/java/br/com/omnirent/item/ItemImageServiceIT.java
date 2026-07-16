package br.com.omnirent.item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.exception.common.GlobalExceptionHandler;
import br.com.omnirent.exception.domain.apptype.StorageErrorType;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.MultipartFileTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.infrastructure.StorageService;
import br.com.omnirent.integration.SpringMvcIntegration;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

@Transactional
@Import(GlobalExceptionHandler.class)
public class ItemImageServiceIT extends SpringMvcIntegration {

	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageService storageService;
    
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private SubCategoryRepository subRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private EntityManager entityManager;
    
	private User user1;

	private Item item1;

	private Address address1;
	
	private Category electronics;
	private SubCategory notebook;
	
	@BeforeEach
	void setUp() {
		user1 = userRepository.save(UserTestFactory.owner());
		address1 = addressRepository.save(AddressTestFactory.forUser(user1));
		electronics = categoryRepository.save(CategoryTestFactory.create("electronics"));
		notebook = subRepository.save(SubCategoryTestFactory.create("notebook", electronics));
		
		item1 = itemRepository.save(ItemTestFactory.create(user1, address1, notebook, "200", ItemCondition.NEW));
	    
		SecurityTestUtils.setAuthenticatedUser(user1);
	}
	
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}

	@Test
	void uploadImages_returnsServiceUnavailableWhenStorageIsDown() throws Exception {

	    when(storageService.upload(any(), anyString()))
	            .thenThrow(
	                    AwsServiceException.builder()
	                            .statusCode(503)
	                            .message("Service unavailable")
	                            .build()
	            );

	    MockMultipartFile request =
	            MultipartFileTestFactory.createRequest("temp1", 1);

	    MockMultipartFile image =
	            MultipartFileTestFactory.image(
	                    "temp1", "image.png", "png");
	    
	    mockMvc.perform(
	            multipart("/item/{itemId}/images", item1.getId())
	                    .file(request)
	                    .file(image)
	                    .with(SecurityTestUtils.auth(user1))
	    )
	    .andExpect(status().isServiceUnavailable());

	    verify(storageService).upload(any(), anyString());
	}
	
	@Test
	void uploadImages_returnsAccessDeniedWhenStorageRejectsRequest() throws Exception {

	    when(storageService.upload(any(), anyString()))
	            .thenThrow(
	                    AwsServiceException.builder()
	                            .statusCode(403)
	                            .message("Access denied")
	                            .build()
	            );

	    MockMultipartFile request =
	            MultipartFileTestFactory.createRequest("temp1", 1);

	    MockMultipartFile image =
	            MultipartFileTestFactory.image(
	                    "temp1", "image.png", "png");

	    mockMvc.perform(
	            multipart("/item/{itemId}/images", item1.getId())
	                    .file(request)
	                    .file(image)
	                    .with(SecurityTestUtils.auth(user1))
	    )
	    .andExpect(status().isForbidden());

	    verify(storageService).upload(any(), anyString());
	}
	
	@Test
	void uploadImages_returnsRateLimitedWhenStorageLimitIsExceeded() throws Exception {

	    when(storageService.upload(any(), anyString()))
	            .thenThrow(
	                    AwsServiceException.builder()
	                            .statusCode(429)
	                            .message("Too many requests")
	                            .build()
	            );

	    MockMultipartFile request =
	            MultipartFileTestFactory.createRequest("temp1", 1);

	    MockMultipartFile image =
	            MultipartFileTestFactory.image(
	                    "temp1", "image.png", "png");

	    mockMvc.perform(
	            multipart("/item/{itemId}/images", item1.getId())
	                    .file(request)
	                    .file(image)
	                    .with(SecurityTestUtils.auth(user1))
	    )
	    .andExpect(status().isTooManyRequests());

	    verify(storageService).upload(any(), anyString());
	}
	
	@Test
	void uploadImages_returnsServiceUnavailableWhenStorageClientFails() throws Exception {

	    when(storageService.upload(any(), anyString()))
	            .thenThrow(
	                    SdkClientException.create("Connection failed")
	            );

	    MockMultipartFile request =
	            MultipartFileTestFactory.createRequest("temp1", 1);

	    MockMultipartFile image =
	            MultipartFileTestFactory.image(
	                    "temp1", "image.png", "png");

	    mockMvc.perform(
	            multipart("/item/{itemId}/images", item1.getId())
	                    .file(request)
	                    .file(image)
	                    .with(SecurityTestUtils.auth(user1))
	    )
	    .andExpect(status().isServiceUnavailable());

	    verify(storageService).upload(any(), anyString());
	}
	
	@Test
	void uploadImages_returnsStorageUploadFailed() throws Exception {
		String targetCode = StorageErrorType.STORAGE_UPLOAD_FAILED.getErrorCode();
		
		when(storageService.upload(any(), anyString()))
        .thenThrow(
                AwsServiceException.builder()
                        .statusCode(400)
                        .message("Bad request")
                        .build()
        );


	    MockMultipartFile request =
	            MultipartFileTestFactory.createRequest("temp1", 1);

	    MockMultipartFile image =
	            MultipartFileTestFactory.image(
	                    "temp1", "image.png", "png");

	    mockMvc.perform(
	            multipart("/item/{itemId}/images", item1.getId())
	                    .file(request)
	                    .file(image)
	                    .with(SecurityTestUtils.auth(user1))
	    )
	    .andExpect(status().isInternalServerError())
	    .andExpect(jsonPath("$.errorCode")
	    	               .value(targetCode));

	    verify(storageService).upload(any(), anyString());
	}
	
}
