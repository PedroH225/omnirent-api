package br.com.omnirent.common.enums;

import java.util.Map;

public enum FieldErrorPriority {	
	REQUIRED,
	FORMAT,
	RANGE,
	BUSINESS;
	
    private static final Map<String, FieldErrorPriority> MAP = Map.of(
            "NotBlank", REQUIRED,
            "NotNull", REQUIRED,

            "Size", RANGE,
            "Min", RANGE,
            "Max", RANGE,

            "Pattern", FORMAT,
            "Email", FORMAT
    );

    public static FieldErrorPriority fromSpringCode(String code) {
        return MAP.getOrDefault(code, BUSINESS);
    }
}
