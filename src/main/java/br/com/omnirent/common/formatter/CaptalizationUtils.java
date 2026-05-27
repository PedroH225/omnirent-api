package br.com.omnirent.common.formatter;

public class CaptalizationUtils {
	
	public static String firstCaptalizedOnly(String text) {
		if (text == null || text.isBlank()) {
			return text;
		}

		text = text.toLowerCase();

		return Character.toUpperCase(text.charAt(0)) +
			   text.substring(1);
	}
}
