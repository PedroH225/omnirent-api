package br.com.omnirent.item;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.common.enums.ItemEnums;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/item")
public class ItemController {

	private ItemService itemService;
	
	@GetMapping("/find/{id}")
	public ItemDetailDTO findById(@PathVariable String id) {
		return itemService.getItemById(id);
	}
	
	@GetMapping("/find/user/me")
	public List<ItemDisplayDTO> findOwnerItems() {
		return itemService.getUserItems();
	}
	
	@GetMapping("/find/user")
	public List<ItemDisplayDTO> findUserItems() {
		return itemService.getUserItems();
	}
	
	@GetMapping("/enums")
	public ItemEnums getEnums() {
		return new ItemEnums();
	}
	
	@PostMapping
	public ItemCreatedDTO addItem(@RequestBody ItemRequestDTO itemDTO) {
		return itemService.addItem(itemDTO);
	}
	
	@PutMapping
	public void updateItem(@RequestBody UpdateItemRequestDTO itemDTO) {
		itemService.updateItem(itemDTO);
	}
	
	@PatchMapping("/changeAddress/{id}/{addressId}")
	public void updateItemAddress(@PathVariable String id, @PathVariable String addressId) {
		itemService.changePickupAddress(id, addressId);;
	}
	
	@PatchMapping("/updateStatus/{itemId}")
	public void updateStatus(@PathVariable String itemId) {
		itemService.updateStatus(itemId);
	}
}
