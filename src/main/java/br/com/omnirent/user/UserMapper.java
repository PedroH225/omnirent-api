package br.com.omnirent.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.EnumOption;
import br.com.omnirent.common.enums.UserEnums;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.user.context.UserAuditSnapshot;
import br.com.omnirent.user.context.UserStatusChangeAuditSnapshot;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserMapper {
	
	private MessageService messageService;

	public List<UserResponseDTO> toDto(List<User> users) {
		return users.stream()
				.map(u -> toDto(u))
				.collect(Collectors.toList());
	}
	
	public UserResponseDTO toDto(User user) {
		UserResponseDTO userDTO = new UserResponseDTO(user);
		return userDTO;
	}
	
	public UserDetailsDTO toDetailsDto(User user) {
		UserDetailsDTO userDTO = new UserDetailsDTO(user);
		return userDTO;
	}
	
	public UserDetails toAuthUser(User context) {
		List<SimpleGrantedAuthority> authorities = context.getRoles().stream()
				.map(a -> new SimpleGrantedAuthority(a.getName()))
				.collect(Collectors.toList());

		AuthMetadata authMetadata = context.getAuthMetadata();
		return (UserDetails) new AuthenticatedUser(
				context.getId(), context.getEmail(),context.getPassword(),authorities,
				authMetadata.getTokenVersion(), authMetadata.getGlobalVersion());
	}

	public UserEnums getLocalizedEnums() {
		List<EnumOption> userStatuses = Arrays.stream(UserStatus.values())
				.map(i -> new EnumOption(i.name(), messageService.get(i.getMessageKey())))
				.collect(Collectors.toList());
		
		return new UserEnums(userStatuses);
	}

	public UserDetailsDTO localize(UserDetailsDTO result) {
		result.setUserStatusLabel(messageService.get(result.getUserStatus().getMessageKey()));
		return result;
	}
	
	public UserAuditSnapshot toAuditSnapshot(UserDetailsDTO userDTO) {
	    return new UserAuditSnapshot(
	            userDTO.getId(),
	            userDTO.getName(),
	            userDTO.getUsername(),
	            userDTO.getEmail(),
	            userDTO.getBirthDate().toString()
	    );
	}
	
	public UserAuditSnapshot toAuditSnapshot(User user) {
	    return new UserAuditSnapshot(
	    		user.getId(), user.getName(),
	    		user.getUsername(), user.getEmail(),
	            user.getBirthDate().toString());
	}
	
	public UserStatusChangeAuditSnapshot toStatusChangeAuditSnapshot(UserStatus status) {
		return new UserStatusChangeAuditSnapshot(status);
	}
}
