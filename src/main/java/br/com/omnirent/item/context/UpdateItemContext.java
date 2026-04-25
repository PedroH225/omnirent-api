package br.com.omnirent.item.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateItemContext {
	private ItemInfo itemInfo;
	
	private String ownerId;
}
