package br.com.omnirent.common.enums;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalEnums {

	private List<EnumOption> rentalPeriods;
	
	private List<EnumOption> rentalStatuses;
}
