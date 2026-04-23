package br.com.omnirent.factory;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.utils.Sequence;

public final class UserTestFactory {

    private UserTestFactory() {}

    public static User owner() {
    	String owner = Sequence.nextString("owner");
        return new User(
        	owner, owner, owner + "@email.com",
        	owner, LocalDate.now(), 1, 1
        );
    }
    
    public static User user() {
    	String owner = Sequence.nextString("user");
        return new User(
        	owner, owner, owner + "@email.com",
        	owner, LocalDate.now(), 1, 1
        );
    }
    
    public static User persistedUser() {
    	User user = user();
    	user.setCreatedAt(LocalDateTime.now());
    	user.setUpdatedAt(LocalDateTime.now());
    	user.setId(Sequence.nextString("userId"));
    	return user;
    }
    
    public static UserDetailsDTO toUserDetails(User user) {
    	return new UserDetailsDTO(user.getId(), user.getName(), user.getUsername(),
    			user.getEmail(), user.getBirthDate(), user.getUserStatus());
    }
    
    public static User fromRequestDto(UserRequestDTO requestDTO, User user) {
    	User updatedUser = new User(requestDTO.name(), requestDTO.username(), requestDTO.email(), user.getPassword(), requestDTO.birthDate(), 1, 1);
    	updatedUser.setId(user.getId());
    	updatedUser.setUserStatus(UserStatus.ACTIVE);
    	return updatedUser;
    }

	public static UserRequestDTO requestDto() {
		String user = Sequence.nextString("user");
		return new UserRequestDTO(user, user, user, LocalDate.now());
	}
}