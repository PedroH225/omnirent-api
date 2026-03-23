package br.com.omnirent.rental;

public class RentalMapper {

	public static RentalResponseDTO toDto(Rental rental) {
		return new RentalResponseDTO(rental);
	}
}
