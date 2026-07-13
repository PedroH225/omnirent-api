package br.com.omnirent.item.dto;

import org.springframework.data.domain.Sort;

import lombok.Getter;

@Getter
public enum ItemFeedSort {
	NEWEST("createdAt", Sort.Direction.DESC),
	PRICE_ASC("itemData.basePrice", Sort.Direction.ASC),
	PRICE_DESC("itemData.basePrice", Sort.Direction.DESC);
	
	private String field;
	
	private Sort.Direction direction;
	
	ItemFeedSort(String field, Sort.Direction direction) {
		this.field = field;
		this.direction = direction;
	}

}
