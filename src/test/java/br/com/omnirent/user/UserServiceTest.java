package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.common.enums.UserEnums;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.DomainEventPublisher;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.ConcurrencyErrorType;
import br.com.omnirent.exception.domain.UserErrorType;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.security.CurrentUserProvider;
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
	private DomainEventPublisher eventPublisher;

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
