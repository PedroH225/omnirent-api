package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private UserMapper mapper;
	
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
		
		when(userRepository.findUserDetailsById(userId)).thenReturn(Optional.of(expected));
		
		UserDetailsDTO result = userService.getUserDetailsById(userId);
		
		assertThat(result).isEqualTo(expected);
		
		verify(userRepository).findUserDetailsById(userId);
		verifyNoMoreInteractions(userRepository);
	}
}
