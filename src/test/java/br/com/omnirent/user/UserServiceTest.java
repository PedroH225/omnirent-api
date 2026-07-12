package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.common.enums.UserEnums;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.config.global.GlobalConfigHolder;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.ConcurrencyErrorType;
import br.com.omnirent.exception.domain.apptype.UserErrorType;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.security.auth.RoleRepository;
import br.com.omnirent.security.domain.Role;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.context.ChangeUserStatusContext;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.user.dto.UserResponseDTO;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserQueryRepository queryRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Mock
	private CurrentUserProvider currentUserProvider;

	@Mock
	private UserValidationService validationService;

	@Mock
	private UserAutorizationService autorizationService;
	
	@Mock
	private SpringDomainEventPublisher eventPublisher;
	
	@Mock
    private RoleRepository roleRepository;

    @Mock
    private GlobalConfigHolder globalConfigHolder;
	
	@Mock
	private Clock clock;
	
    private final Instant fixedInstant = Instant.parse("2023-01-01T10:00:00Z");
    
    private User user;
    
    private Role defaultRole = new Role();
    
    @Mock
    private AppProperties appProperties;
    
    @BeforeEach
    void setUp() {
    	user = UserTestFactory.persistedUser();
    	
    	defaultRole.setId(1);
    	defaultRole.setName("ROLE_USER");
    }
	
    @Test
    void createUser_WithAllValidFields_ShouldCreateAndPublishEvent() {
    	User data = UserTestFactory.copy(user);

        when(userRepository.existsByUsername(data.getUsername())).thenReturn(false);
        when(globalConfigHolder.getGlobalTokenVersion()).thenReturn(1);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        User result = userService.createUser(data.getName(), data.getUsername(),
        		data.getEmail(), data.getPassword(), data.getBirthDate(),
        		data.getLocale(), data.getTimezone());
        
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());
        verify(eventPublisher).publish(any(UserRegisteredEvent.class));
        
        User saved = captor.getValue();

        assertThat(saved.getRoles()).contains(defaultRole);
        assertThat(saved)
	        .usingRecursiveComparison()
	        .isEqualTo(data);
        assertThat(result)
	        .usingRecursiveComparison()
	        .isEqualTo(user);
    }
    
    @Test
    void createUser_GeneratingNullFields() {
    	when(appProperties.locale()).thenReturn("pt-BR");
    	when(appProperties.timezone()).thenReturn("America/Sao_Paulo");
    	
    	User data = UserTestFactory.copy(user);
    	data.setName(null);
    	data.setUsername(null);
    	data.setLocale(null);   
    	data.setTimezone(null);
    	
    	when(userRepository.existsByUsername(any())).thenReturn(false);
        when(globalConfigHolder.getGlobalTokenVersion()).thenReturn(1);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        User result = userService.createUser(data.getName(), data.getUsername(),
        		data.getEmail(), data.getPassword(), data.getBirthDate(),
        		data.getLocale(), data.getTimezone());
        
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());
        
        assertNotNull(result.getName());
        assertNotNull(result.getUsername());
        assertNotNull(result.getLocale());
        assertNotNull(result.getTimezone());   
    }
    
    @Test
    void createUser_InvalidTimezoneAndLocale() {
    	when(appProperties.locale()).thenReturn("pt-BR");
    	when(appProperties.timezone()).thenReturn("America/Sao_Paulo");
    	
    	User data = UserTestFactory.copy(user);
    	data.setLocale("eng-USA");   
    	data.setTimezone("Amer/new_York");
    	
    	when(userRepository.existsByUsername(any())).thenReturn(false);
        when(globalConfigHolder.getGlobalTokenVersion()).thenReturn(1);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        User result = userService.createUser(data.getName(), data.getUsername(),
        		data.getEmail(), data.getPassword(), data.getBirthDate(),
        		data.getLocale(), data.getTimezone());
                
        assertEquals(result.getLocale(), "pt-BR");
        assertNotNull(result.getTimezone(), "America/Sao_Paulo");   
    }

	@Test
	void shouldThrowExceptionWhenUserDoesNotExist() {
		String userId = "invalid-id";
		String expectedErrorType = UserErrorType.NOT_FOUND.getErrorType();

		when(queryRepository.verifyUser(userId)).thenReturn(false);

		assertThatThrownBy(() -> userService.requireExistence(userId))
		.isInstanceOf(ApiException.class)
        .satisfies(ex -> {
            ApiException exception = (ApiException) ex;

            assertThat(exception.getErrorType()).isEqualTo(expectedErrorType);
        });
	}

	@Test
	void shouldReturnUserReference() {
		User expectedUser = UserTestFactory.persistedUser();
		when(userRepository.getReferenceById(expectedUser.getId())).thenReturn(expectedUser);

		User result = userService.getUserReference(expectedUser.getId());

		assertThat(result).isEqualTo(expectedUser);
	}

	@Test
	void shouldReturnValidUserReferenceWhenExists() {
		User expectedUser = UserTestFactory.persistedUser();
		when(queryRepository.verifyUser(expectedUser.getId())).thenReturn(true);
		when(userRepository.getReferenceById(expectedUser.getId())).thenReturn(expectedUser);

		User result = userService.getValidReference(expectedUser.getId());

		assertThat(result).isEqualTo(expectedUser);
	}

	@Test
	void shouldReturnLocalizedUserDetails() {
		User user = UserTestFactory.persistedUser();
		UserDetailsDTO detailsResult = UserTestFactory.toUserDetails(user);
		UserDetailsDTO localizedDetails = UserTestFactory.toUserDetails(user);

		when(currentUserProvider.currentUserId()).thenReturn(user.getId());
		when(queryRepository.findUserDetailsById(user.getId())).thenReturn(Optional.of(detailsResult));
		when(userMapper.localize(detailsResult)).thenReturn(localizedDetails);

		UserDetailsDTO result = userService.getUserDetailsById();

		assertThat(result).isEqualTo(localizedDetails);
	}

	@Test
	void shouldThrowExceptionWhenUserDetailsNotFound() {
		String userId = "invalid-id";
		String expectedErrorType = UserErrorType.NOT_FOUND.getErrorType();

		
		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(queryRepository.findUserDetailsById(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.getUserDetailsById())
			.isInstanceOf(ApiException.class)
	        .satisfies(ex -> {
	            ApiException exception = (ApiException) ex;
	
	            assertThat(exception.getErrorType()).isEqualTo(expectedErrorType);
	        });
	}

	@Test
	void shouldReturnAllUsers() {
		List<UserResponseDTO> expectedList = List.of(mock(UserResponseDTO.class));
		when(queryRepository.findAllUser()).thenReturn(expectedList);

		List<UserResponseDTO> result = userService.findAll();

		assertThat(result).isEqualTo(expectedList);
	}

	@Test
	void shouldUpdateUserAndReturnDetails() {
		User user = UserTestFactory.persistedUser();
		UserRequestDTO request = UserTestFactory.requestDto();
		UserDetailsDTO detailsResult = UserTestFactory.toUserDetails(user);
		UserDetailsDTO expectedLocalizedResult = UserTestFactory.toUserDetails(user);

		when(currentUserProvider.currentUserId()).thenReturn(user.getId());
		when(queryRepository.verifyUser(user.getId())).thenReturn(true);
		when(userRepository.updateUser(user.getId(), request.name(), request.username(), request.email(), request.birthDate())).thenReturn(1);
		when(queryRepository.findUserDetailsById(user.getId())).thenReturn(Optional.of(detailsResult));
		when(userMapper.localize(detailsResult)).thenReturn(expectedLocalizedResult);

		UserDetailsDTO result = userService.update(request);

		assertThat(result).isEqualTo(expectedLocalizedResult);
		verify(validationService).validateTakenFields(user.getId(), request);
	}

	@Test
	void shouldThrowExceptionWhenUpdateFailsDueToOptimisticLock() {
		User user = UserTestFactory.persistedUser();
		UserRequestDTO request = UserTestFactory.requestDto();
		String expectedErrorType = ConcurrencyErrorType.OPTMISTIC_LOCK.getErrorType();

		when(currentUserProvider.currentUserId()).thenReturn(user.getId());
		when(queryRepository.verifyUser(user.getId())).thenReturn(true);
		when(userRepository.updateUser(user.getId(), request.name(), request.username(), request.email(), request.birthDate())).thenReturn(0);

		assertThatThrownBy(() -> userService.update(request))
		.isInstanceOf(ApiException.class)
	        .satisfies(ex -> {
	            ApiException exception = (ApiException) ex;
	
	            assertThat(exception.getErrorType()).isEqualTo(expectedErrorType);
	        });
		verify(validationService).validateTakenFields(user.getId(), request);
	}
	
	@Test
	void shouldThrowExceptionWhenUserIsBannedOnStatusChange() {
		String userId = "valid-id";
		ChangeUserStatusContext context = mock(ChangeUserStatusContext.class);
		String expectedErrorType = UserErrorType.BANNED.getErrorType();

		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(queryRepository.getUserStatusChangeContext(userId)).thenReturn(Optional.of(context));
		when(context.currentUserStatus()).thenReturn(UserStatus.BANNED);
		
		doThrow(new ApiException(UserErrorType.BANNED))
				.when(autorizationService).requireNotBanned(UserStatus.BANNED);

		assertThatThrownBy(() -> userService.changeUserStatus())
				.isInstanceOf(ApiException.class)
				.satisfies(ex -> {
					ApiException exception = (ApiException) ex;
					assertThat(exception.getErrorType()).isEqualTo(expectedErrorType);
				});

		verify(autorizationService).requireNotBanned(UserStatus.BANNED);
		verifyNoInteractions(userRepository);
	}
	
	@Test
	void shouldChangeUserStatusToInactiveWhenCurrentlyActive() {
		String userId = "valid-id";
		ChangeUserStatusContext context = mock(ChangeUserStatusContext.class);

		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(queryRepository.getUserStatusChangeContext(userId)).thenReturn(Optional.of(context));
		when(context.currentUserStatus()).thenReturn(UserStatus.ACTIVE);
		when(userRepository.updateUserStatus(userId, UserStatus.ACTIVE, UserStatus.INACTIVE)).thenReturn(1);

		userService.changeUserStatus();

		verify(autorizationService).requireNotBanned(UserStatus.ACTIVE);
		verify(userRepository).updateUserStatus(userId, UserStatus.ACTIVE, UserStatus.INACTIVE);
	}

	@Test
	void shouldChangeUserStatusToActiveWhenCurrentlyInactive() {
		String userId = "valid-id";
		ChangeUserStatusContext context = mock(ChangeUserStatusContext.class);

		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(queryRepository.getUserStatusChangeContext(userId)).thenReturn(Optional.of(context));
		when(context.currentUserStatus()).thenReturn(UserStatus.INACTIVE);
		when(userRepository.updateUserStatus(userId, UserStatus.INACTIVE, UserStatus.ACTIVE)).thenReturn(1);
		
		userService.changeUserStatus();

		verify(autorizationService).requireNotBanned(UserStatus.INACTIVE);
		verify(userRepository).updateUserStatus(userId, UserStatus.INACTIVE, UserStatus.ACTIVE);
	}

	@Test
	void shouldThrowExceptionWhenStatusChangeContextNotFound() {
		String userId = "invalid-id";
		String expectedErrorType = UserErrorType.NOT_FOUND.getErrorType();
		
		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(queryRepository.getUserStatusChangeContext(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.changeUserStatus())
        .isInstanceOf(ApiException.class)
        .satisfies(ex -> {
            ApiException exception = (ApiException) ex;

            assertThat(exception.getErrorType()).isEqualTo(expectedErrorType);
        });
	}

	@Test
	void shouldThrowExceptionWhenStatusChangeFailsDueToOptimisticLock() {
		String userId = "valid-id";
		ChangeUserStatusContext context = mock(ChangeUserStatusContext.class);
		String expectedErrorType = ConcurrencyErrorType.OPTMISTIC_LOCK.getErrorType();

		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(queryRepository.getUserStatusChangeContext(userId)).thenReturn(Optional.of(context));
		when(context.currentUserStatus()).thenReturn(UserStatus.ACTIVE);
		when(userRepository.updateUserStatus(userId, UserStatus.ACTIVE, UserStatus.INACTIVE)).thenReturn(0);

		assertThatThrownBy(() -> userService.changeUserStatus())
			.isInstanceOf(ApiException.class)
	        .satisfies(ex -> {
	            ApiException exception = (ApiException) ex;
	
	            assertThat(exception.getErrorType()).isEqualTo(expectedErrorType);
	        });
		verify(autorizationService).requireNotBanned(UserStatus.ACTIVE);
	}

	@Test
	void shouldReturnLocalizedEnums() {
		UserEnums expectedEnums = mock(UserEnums.class);
		when(userMapper.getLocalizedEnums()).thenReturn(expectedEnums);

		UserEnums result = userService.getEnums();

		assertThat(result).isEqualTo(expectedEnums);
	}
}
