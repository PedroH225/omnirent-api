package br.com.omnirent.common.enums;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEnums {

	private List<EnumOption> userStatuses;
}
