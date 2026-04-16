package br.com.omnirent.rental.context;

import br.com.omnirent.common.enums.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalStatusChangeContext {

	private String id;
	
	private String ownerId;
		
	private String renterId;
	
	private RentalStatus rentalStatus;
	
}
