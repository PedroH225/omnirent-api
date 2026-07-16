package br.com.omnirent.item.context;

import java.util.List;

import br.com.omnirent.item.domain.ItemImageRequestDto;

public record ItemImagesRequestDto(
		List<ItemImageRequestDto> images) {

}
