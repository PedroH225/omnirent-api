package br.com.omnirent.common.formatter;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class IdentityGeneratorUtil {
	
	private static String extractBaseUsername(String email) {
	    if (email == null || !email.contains("@")) {
	        return email;
	    }

	    return email.substring(0, email.indexOf("@"));
	}

	public static String generateNameFromEmail(String email) {
	    String cleanName = extractBaseUsername(email)
	            .replaceAll("[._-]", " ");

	    return Arrays.stream(cleanName.split(" "))
	            .filter(word -> !word.isEmpty())
	            .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
	            .collect(Collectors.joining(" "));
	}

	public static String generateUniqueUsername(String email) {
	    String baseUsername = extractBaseUsername(email)
	            .replaceAll("[^a-zA-Z0-9]", "");

	    int randomNumber = ThreadLocalRandom.current().nextInt(1000, 10000);

	    return baseUsername + randomNumber;
	}
}
