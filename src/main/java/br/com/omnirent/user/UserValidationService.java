package br.com.omnirent.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.omnirent.exception.common.ValidationException;
import br.com.omnirent.exception.domain.CommonErrorType;
import br.com.omnirent.exception.domain.FieldErrorResponse;
import br.com.omnirent.user.context.UserTakenContext;
import br.com.omnirent.user.domain.UserIdentityInput;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserValidationService {
	
	private UserQueryRepository queryRepository;

	public void validateTakenFields(UserIdentityInput user) {
		String validationPrefix = "validation.field.";
		
		List<FieldErrorResponse> takenFields = new ArrayList<>();
		
	    List<UserTakenContext> conflictingUsers = queryRepository.findTakenFields(
	    		user.getUsername(),
	    		user.getEmail()
	            );		

	    boolean usernameTaken = conflictingUsers.stream()
				.anyMatch(c -> c.username().equals(user.getUsername()));
		
		boolean emailTaken = conflictingUsers.stream()
				.anyMatch(c -> c.email().equalsIgnoreCase(user.getEmail()));
		
		if (usernameTaken) {
			takenFields.add(new FieldErrorResponse("username", validationPrefix + "taken"));
		}
		
		if (emailTaken) {
			takenFields.add(new FieldErrorResponse("email", validationPrefix + "taken"));
		}
		
		if (!takenFields.isEmpty()) {
			throw new ValidationException(
					CommonErrorType.VALIDATION_ERROR, takenFields, user.getClass().getSimpleName());
		}
	}
}
