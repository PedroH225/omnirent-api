package br.com.omnirent.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.omnirent.exception.common.ValidationException;
import br.com.omnirent.exception.domain.CommonErrorType;
import br.com.omnirent.exception.domain.FieldErrorResponse;
import br.com.omnirent.user.context.UserTakenContext;
import br.com.omnirent.user.domain.UserIdentityInput;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidationService {
	
	private final UserQueryRepository queryRepository;

	private static String VALIDATION_PREFIX = "validation.field.";
	
	public void validateTakenFields(String userId, UserIdentityInput user) {
		List<FieldErrorResponse> takenFields = new ArrayList<>();
		
	    List<UserTakenContext> conflictingUsers = findConflictingUsers(userId, user);
	    
	    boolean usernameTaken = conflictingUsers.stream()
				.anyMatch(c -> c.username().equals(user.getUsername()));
		
		boolean emailTaken = conflictingUsers.stream()
				.anyMatch(c -> c.email().equalsIgnoreCase(user.getEmail()));
		
		if (usernameTaken) {
			takenFields.add(new FieldErrorResponse("username", VALIDATION_PREFIX + "taken", null));
		}
		
		if (emailTaken) {
			takenFields.add(new FieldErrorResponse("email", VALIDATION_PREFIX + "taken", null));
		}
		
		if (!takenFields.isEmpty()) {
			throw new ValidationException(
					CommonErrorType.VALIDATION_ERROR, takenFields, user.getClass().getSimpleName());
		}
	}
	
	public void validatePasswordMatch(String password, String repeatedPassword) {
		if (!password.equals(repeatedPassword)) {
			FieldErrorResponse invalidField =
					new FieldErrorResponse("repeatedPassword", VALIDATION_PREFIX + "password.mismatch", null);
			
			throw new ValidationException(
					CommonErrorType.VALIDATION_ERROR, Arrays.asList(invalidField), "user");
		}
	}
	
	private List<UserTakenContext> findConflictingUsers(
			String userId, UserIdentityInput user) {
	    if (userId != null) { 
	    	return queryRepository.findTakenFieldsNotId(userId, user.getUsername(), user.getEmail());		
		} 
	    
	    return queryRepository.findTakenFields(user.getUsername(),user.getEmail());		
	}
}
