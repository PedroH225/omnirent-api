package br.com.omnirent.common.enums;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

@Data
public final class ItemEnums {
	
	private List<String> itemStatuses;
	
	private List<String> itemConditions;

	public ItemEnums() {
		this.itemStatuses = Arrays.stream(ItemStatus.values())
				.map(e -> e.toString())
				.toList();
		this.itemConditions = Arrays.stream(ItemCondition.values())
				.map(e -> e.toString())
				.toList();
	}
}
