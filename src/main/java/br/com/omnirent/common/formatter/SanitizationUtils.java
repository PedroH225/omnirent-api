package br.com.omnirent.common.formatter;

public class SanitizationUtils {

	public static String name(String name) {
		return name != null ?
				name.strip()
				.replaceAll("\\s+", " ")
				: null;
	}
	
	public static String username(String username) {
		return username != null ? 
				username.strip()
				.replaceAll("\\s+", "")
				.toLowerCase() 
				: null;
	}
		
	public static String email(String email) {
		return email != null ?
				email.strip()
				.replaceAll("\\s+", "")
				.toLowerCase()
				: null;
	}
	
	
	public static String text(String text) {
		return text != null ?
				text.strip()
				.replaceAll("\\s+", " ")
				: null;
	}
	
	public static String description(String text) {
		return text != null
				? text.strip()
				: null;
	}
	
	public static String identifier(String identifier) {
		return identifier != null
				? identifier.strip()
				: null;
	}
}
