package br.com.omnirent.factory;

import java.time.LocalDate;

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
}