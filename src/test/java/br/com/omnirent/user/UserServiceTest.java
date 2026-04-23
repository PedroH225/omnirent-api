package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.exception.domain.UserNotFoundException;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private UserMapper mapper;
	
	@Mock
	private CurrentUserProvider currentUserProvider;
	
	private User user1;
	private User user2;

	@BeforeEach
	void setUp() {
		user1 = UserTestFactory.persistedUser();
		user1.setUserStatus(UserStatus.ACTIVE);
	    user2 = UserTestFactory.persistedUser();
	    user2.setUserStatus(UserStatus.ACTIVE);
	}
	
	@Test
	void shouldGetUserDetails() {
		String userId = user1.getId();
		UserDetailsDTO expected = UserTestFactory.toUserDetails(user1);
		
		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(userRepository.findUserDetailsById(userId)).thenReturn(Optional.of(expected));
		
		UserDetailsDTO result = userService.getUserDetailsById();
		
		assertThat(result).isEqualTo(expected);
		
		verify(currentUserProvider).currentUserId();
		verify(userRepository).findUserDetailsById(userId);
		verifyNoMoreInteractions(userRepository, currentUserProvider);
	}
	
	@Test
	void shouldThrowWhenUserNotFound() {
		String invalidId = "invalid-id";
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);
		when(userRepository.findUserDetailsById(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> userService.getUserDetailsById())
		.isInstanceOf(UserNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userRepository).findUserDetailsById(invalidId);
		verifyNoMoreInteractions(userRepository, currentUserProvider);
	}
	
	@Test
	void shouldUpdateUser() {
		String userId = user1.getId();
		
		UserRequestDTO request = UserTestFactory.requestDto();
		
		User expected = UserTestFactory.fromRequestDto(request, user1);
		UserDetailsDTO expectedDto = UserTestFactory.toUserDetails(expected);
		
		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
	    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    when(mapper.toDetailsDto(any(User.class)))
	    .thenAnswer(invocation -> {
	        User u = invocation.getArgument(0, User.class);
	        return UserTestFactory.toUserDetails(u);
	    });
	   
	    UserDetailsDTO result = userService.update(request);

	    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

	    verify(currentUserProvider).currentUserId();
	    verify(userRepository).findById(userId);
	    verify(userRepository).save(userCaptor.capture());
	    verify(mapper).toDetailsDto(userCaptor.getValue());
	    verifyNoMoreInteractions(userRepository, mapper, currentUserProvider);

	    User saved = userCaptor.getValue();

	    assertThat(result).isEqualTo(expectedDto);

	    assertThat(saved).isNotNull();
	    assertThat(saved.getId()).isEqualTo(user1.getId());
	    assertThat(saved.getEmail()).isEqualTo(request.email());
	    assertThat(saved.getName()).isEqualTo(request.name());
	    assertThat(saved.getUsername()).isEqualTo(request.username());

	    assertThat(saved.getEmail()).isEqualTo(result.getEmail());
	    assertThat(saved.getName()).isEqualTo(result.getName());
	    assertThat(saved.getUsername()).isEqualTo(result.getUsername());
	}
	
	@Test
	void shouldThrowWhenUserNotFoundOnUpdate() {
		String invalidId = "invalid-id";
		
		UserRequestDTO request = UserTestFactory.requestDto();
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);
		when(userRepository.findById(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> userService.update(request))
		.isInstanceOf(UserNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userRepository).findById(invalidId);
		verifyNoInteractions(mapper);
		verifyNoMoreInteractions(userRepository, currentUserProvider);
	}
	
	@Test
	void shouldDeactivateUser() {
		String userId = user1.getId();
		
		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		assertThat(user1.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
		
		userService.deactivateUser();
		
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userRepository).findById(userId);
		verify(userRepository).save(userCaptor.capture());
		verifyNoMoreInteractions(userRepository, currentUserProvider);
		
		assertThat(userCaptor.getValue().getUserStatus()).isEqualTo(UserStatus.INACTIVE);
	}
	
	@Test
	void shouldActivateUser() {
		String userId = user1.getId();
		user1.setUserStatus(UserStatus.INACTIVE);
		
		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		assertThat(user1.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
		
		userService.activateUser();
		
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userRepository).findById(userId);
		verify(userRepository).save(userCaptor.capture());
		verifyNoMoreInteractions(userRepository, currentUserProvider);
		
		assertThat(userCaptor.getValue().getUserStatus()).isEqualTo(UserStatus.ACTIVE);
	}
	
	@Test
	void shouldThrowWhenUserNotFoundOnActivate() {
		String invalidId = "invalid-id";
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);
		when(userRepository.findById(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> userService.activateUser())
		.isInstanceOf(UserNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userRepository).findById(invalidId);
		verifyNoMoreInteractions(userRepository, currentUserProvider);
	}
	
	@Test
	void shouldThrowWhenUserNotFoundOnDeactivate() {
		String invalidId = "invalid-id";
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);
		when(userRepository.findById(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> userService.deactivateUser())
		.isInstanceOf(UserNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userRepository).findById(invalidId);
		verifyNoMoreInteractions(userRepository, currentUserProvider);
	}
}
