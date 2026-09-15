package br.com.omnirent.item;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.common.enums.EnumOption;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.common.page.PageResponseDTO;
import br.com.omnirent.item.context.ItemRejectedRequestDto;
import br.com.omnirent.item.context.SearchItemFilter;
import br.com.omnirent.item.dto.ItemAnalisysDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import lombok.RequiredArgsConstructor;

@RequestMapping("/admin/items")
@RestController
@RequiredArgsConstructor
public class ItemAdminController {
	
	private final ItemService itemService;
	
	@GetMapping
	public PageResponseDTO<ItemDisplayDTO> searchItems(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) ItemStatus itemStatus, Pageable pageable) {
		SearchItemFilter searchFilters = new SearchItemFilter(name, itemStatus);
		return itemService.searchItems(searchFilters, pageable);
	}	

	@GetMapping("/analisys")
	public PageResponseDTO<ItemAnalisysDTO> findItemUnderAnalisys(Pageable pageable) {
		return itemService.getUnderAnalisys(pageable);
	}
	
	@GetMapping("/enums")
	public List<EnumOption> getRejectedEnums() {
		return itemService.getRejectedReasonEnums();
	}
	
	
	@PatchMapping("/approve/{itemId}")
	public void approveItem(@PathVariable String itemId) {
		itemService.approveItem(itemId);
	}
	
	@PatchMapping("/reject/{itemId}")
	public void rejectItem(
			@PathVariable String itemId, @RequestBody ItemRejectedRequestDto rejectedDto) {
		itemService.rejectItem(itemId, rejectedDto);
	}
}
