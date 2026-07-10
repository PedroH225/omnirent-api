package br.com.omnirent.factory;

import java.time.*;

import br.com.omnirent.common.enums.*;
import br.com.omnirent.security.domain.*;
import br.com.omnirent.user.domain.*;
import br.com.omnirent.user.dto.*;
import br.com.omnirent.utils.*;

public final class UserTestFactory {

    private UserTestFactory() {}

    private static User createUser(String prefix) {
        String value = Sequence.nextString(prefix);

        AuthMetadata authMetadata = new AuthMetadata();
        authMetadata.setGlobalVersion(1);
        authMetadata.setTokenVersion(1);

        User user = new User();
        user.setName(value);
        user.setUsername(value);
        user.setEmail(value + "@email.com");
        user.setPassword(value);
        user.setBirthDate(LocalDate.now());
        user.setAuthMetadata(authMetadata);
        
        user.setLocale("pt-BR");
        user.setTimezone("America/Sao_Paulo");
        
        Role role = new Role();
    	role.setId(1);
    	role.setName("ROLE_USER");
    	user.getRoles().add(role);

        return user;
    }

    public static User owner() {
        return createUser("owner");
    }

    public static User user() {
        return createUser("user");
    }
    
    public static User persistedUser() {
    	User user = user();
    	user.setCreatedAt(Instant.now());
    	user.setUpdatedAt(Instant.now());
    	user.setId(Sequence.nextString("userId"));
    	return user;
    }
    
    public static User persistedOwner() {
    	User user = user();
    	user.setCreatedAt(Instant.now());
    	user.setUpdatedAt(Instant.now());
    	user.setId(Sequence.nextString("ownerId"));
    	return user;
    }
    public static UserDetailsDTO toUserDetails(User user) {
    	return new UserDetailsDTO(user.getId(), user.getName(), user.getUsername(),
    			user.getEmail(), user.getBirthDate(), user.getUserStatus());
    }
    
    public static User fromRequestDto(UserRequestDTO requestDTO, User user) {
        AuthMetadata authMetadata = new AuthMetadata();
        authMetadata.setGlobalVersion(1);
        authMetadata.setTokenVersion(1);

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setName(requestDTO.name());
        updatedUser.setUsername(requestDTO.username());
        updatedUser.setEmail(requestDTO.email());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setBirthDate(requestDTO.birthDate());
        updatedUser.setUserStatus(UserStatus.ACTIVE);
        updatedUser.setAuthMetadata(authMetadata);

        return updatedUser;
    }

	public static UserRequestDTO requestDto() {
		String user = Sequence.nextString("user");
		return new UserRequestDTO(user, user, user, LocalDate.now());
	}

	public static UserRequestDTO requestDtoBuilder(
	        String name, String username, String email, LocalDate birthDate) {

	    return new UserRequestDTO(name, username, email, birthDate);
	}

	public static User copy(User data) {
        User user = new User();
        user.setName(data.getName());
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setPassword(data.getPassword());
        user.setBirthDate(data.getBirthDate());
        user.setAuthMetadata(data.getAuthMetadata());
        user.setRoles(data.getRoles());
        user.setLocale(data.getLocale());
        user.setTimezone(data.getTimezone());
        return user;
	}
}