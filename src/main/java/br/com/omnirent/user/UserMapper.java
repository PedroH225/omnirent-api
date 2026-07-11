package br.com.omnirent.user;

import java.time.LocalDate;
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

	public UserResponseDTO toDto(User user) {
		UserResponseDTO userDTO = new UserResponseDTO(user);
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
	            resolveBirthdateStr(userDTO.getBirthDate()));
	}
	
	public UserAuditSnapshot toAuditSnapshot(User user) {
	    return new UserAuditSnapshot(
	    		user.getId(), user.getName(),
	    		user.getUsername(), user.getEmail(),
	    		resolveBirthdateStr(user.getBirthDate()));
	}
	
	public UserStatusChangeAuditSnapshot toStatusChangeAuditSnapshot(UserStatus status) {
		return new UserStatusChangeAuditSnapshot(status);
	}
	
	private String resolveBirthdateStr(LocalDate birthDate) {
		return birthDate != null ?
				birthDate.toString() : null;
	}
}
