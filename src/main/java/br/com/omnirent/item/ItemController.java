package br.com.omnirent.item;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemEnums;
import br.com.omnirent.common.page.PageResponseDTO;
import br.com.omnirent.item.context.ItemFeedFilter;
import br.com.omnirent.item.context.ItemImagesRequestDto;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.item.dto.ItemFeedDTO;
import br.com.omnirent.item.dto.ItemFeedSort;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.ItemUpdatedDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/item")
public class ItemController {

	private final ItemService itemService;
	
	private final ItemImageService imageService;
	
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
		return itemService.getEnums();
	}
	
	@GetMapping("/feed")
	public PageResponseDTO<ItemFeedDTO> getItemFeed(
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String category,
	        @RequestParam(required = false) String subCategory,
	        @RequestParam(required = false) ItemCondition itemCondition,
	        @RequestParam(name = "sort", required = false) ItemFeedSort itemFeedSort,
	        Pageable pageable) {

	    ItemFeedFilter feedFilter =
	            new ItemFeedFilter(name, category, subCategory, itemCondition);

	    pageable = resolvePageSort(pageable, itemFeedSort);

	    return itemService.getItemFeed(feedFilter, pageable);
	}
	
	@PostMapping
	public ItemCreatedDTO addItem(@RequestBody @Valid ItemRequestDTO itemDTO) {
		return itemService.addItem(itemDTO);
	}
	
	@PostMapping(value = "/{itemId}/images",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void saveImages(
	        @PathVariable String itemId,
	        @RequestPart(name = "request", required = false) ItemImagesRequestDto request,
	        MultipartHttpServletRequest multipartRequest) throws IOException {
		Map<String, MultipartFile> files = multipartRequest.getFileMap();
		files.remove("request");
	    imageService.saveImages(request.images(), files, itemId);
	}
	
	@PutMapping
	public ItemUpdatedDTO updateItem(@RequestBody @Valid UpdateItemRequestDTO itemDTO) {
		return itemService.updateItem(itemDTO);
	}
	
	@PatchMapping("/changeAddress/{id}/{addressId}")
	public void updateItemAddress(@PathVariable String id, @PathVariable String addressId) {
		itemService.changePickupAddress(id, addressId);;
	}
	
	@PatchMapping("/changeSubCategory/{id}/{subCategoryId}")
	public void updateItemSubCategory(@PathVariable String id, @PathVariable String subCategoryId) {
		itemService.changeSubCategory(id, subCategoryId);;
	}
	
	@PatchMapping("/changeAvailability/{itemId}")
	public void changeAvailability(@PathVariable String itemId) {
		itemService.changeAvailability(itemId);
	}
	
	@PatchMapping("/aprove/{itemId}")
	public void aproveItem(@PathVariable String itemId) {
		itemService.aproveItem(itemId);
	}
	
	private Pageable resolvePageSort(Pageable pageable, ItemFeedSort itemFeedSort) {
		itemFeedSort = itemFeedSort == null ? ItemFeedSort.NEWEST : itemFeedSort;

	    return PageRequest.of(
	            pageable.getPageNumber(),
	            pageable.getPageSize(),
	            Sort.by(itemFeedSort.getDirection(), itemFeedSort.getField())
	    );
	}
}
