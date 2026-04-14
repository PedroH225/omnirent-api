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
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.security.SecurityUtils;
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
	
	@GetMapping("/find/user")
	public List<ItemDetailDTO> findUserItems() {
		return itemService.getUserItems(SecurityUtils.currentUserId());
	}
	
	@GetMapping("/enums")
	public ItemEnums getEnums() {
		return new ItemEnums();
	}
	
	@PostMapping
	public ItemDetailDTO addItem(@RequestBody ItemRequestDTO itemDTO) {
		return itemService.addItem(itemDTO, SecurityUtils.currentUserId());
	}
	
	@PutMapping
	public ItemDetailDTO updateItem(@RequestBody ItemRequestDTO itemDTO) {
		return itemService.updateItem(itemDTO, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/updateStatus/{itemId}/{itemStatus}")
	public ItemDetailDTO updateStatus(@PathVariable String itemId, @PathVariable String itemStatus) {
		return itemService.updateStatus(itemId, itemStatus, SecurityUtils.currentUserId());
	}
}
