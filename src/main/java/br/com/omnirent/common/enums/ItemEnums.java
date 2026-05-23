package br.com.omnirent.common.enums;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemEnums {

	List<EnumOption> itemConditions;
	
	List<EnumOption> itemStatuses;
}
