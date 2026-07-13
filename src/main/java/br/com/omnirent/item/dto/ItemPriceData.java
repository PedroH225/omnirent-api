package br.com.omnirent.item.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemPriceData {

	private BigDecimal hourPrice;
	
	private BigDecimal dailyPrice;

	private BigDecimal weeklyPrice;

	private BigDecimal monthlyPrice;
}
