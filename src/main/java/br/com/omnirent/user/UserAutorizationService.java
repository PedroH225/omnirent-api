package br.com.omnirent.user;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.UserErrorType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserAutorizationService {
	
	public void requireNotBanned(UserStatus currentStatus) {
		if (currentStatus == UserStatus.BANNED) {
			throw new ApiException(UserErrorType.BANNED);
		}
	}
}
