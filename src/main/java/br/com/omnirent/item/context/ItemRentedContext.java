package br.com.omnirent.item.context;

import br.com.omnirent.address.context.AddressInfo;
import lombok.Data;

@Data
public class ItemRentedContext {

	private ItemInfo itemInfo;
	
	private AddressInfo addressInfo;
	
	private String ownerId;
	
	private String ownerName;

	public ItemRentedContext(ItemInfo itemInfo, AddressInfo addressInfo, String ownerId, String ownerName) {
		this.itemInfo = itemInfo;
		this.addressInfo = addressInfo;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
	}
	
	
}
