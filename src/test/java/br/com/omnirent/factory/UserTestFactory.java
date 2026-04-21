package br.com.omnirent.factory;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.omnirent.user.domain.User;
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
}