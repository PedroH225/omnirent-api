package br.com.omnirent.item.dto;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ItemRequestDTO(
		String id,

	    @NotBlank(message = "blank")
	    @Size(min = 3, max = 100, message = "size")
	    String name,

	    @NotBlank(message = "blank")
	    @Size(max = 50, message = "max_size")
	    String model,

	    @NotBlank(message = "blank")
	    @Size(max = 50, message = "max_size")
	    String brand,

	    @Size(max = 1000, message = "max_size")
	    String description,

	    @NotNull(message = "required")
	    @Positive(message = "price.invalid")
	    BigDecimal basePrice,

	    @NotNull(message = "required")
	    ItemCondition itemCondition,

	    @NotBlank(message = "required")
	    String subCategoryId,

	    @NotBlank(message = "blank")
	    String addressId
		) {}
