package br.com.omnirent.item;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.security.SecurityUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/item")
public class ItemController {

	private ItemService itemService;
	
	@GetMapping("/find/{id}")
	public ItemResponseDTO findById(@PathVariable String id) {
		return itemService.getItemById(id);
	}
	
	@GetMapping("/find/user")
	public List<ItemResponseDTO> findUserItems() {
		return itemService.getUserItems(SecurityUtils.currentUserId());
	}
	
	@PostMapping
	public ItemResponseDTO addItem(@RequestBody ItemRequestDTO itemDTO) {
		return itemService.addItem(itemDTO, SecurityUtils.currentUserId());
	}
	
	@PutMapping
	public ItemResponseDTO updateItem(@RequestBody ItemRequestDTO itemDTO) {
		return itemService.updateItem(itemDTO, SecurityUtils.currentUserId());
	}
}
