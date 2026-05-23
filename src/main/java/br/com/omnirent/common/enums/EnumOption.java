package br.com.omnirent.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class EnumOption {
	
	private String code;
	
	private String label;
}
