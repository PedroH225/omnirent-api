package br.com.omnirent.address;

import java.util.List;
import java.util.stream.Collectors;

import br.com.omnirent.rental.domain.Rental;

public class AddressMapper {

	public static List<AddressResponseDTO> toDto(List<Address> addresses) {
		return addresses.stream()
				.map(AddressResponseDTO::new)
				.collect(Collectors.toList());
	}
	
	public static AddressResponseDTO toDto(Address address) {
		return new AddressResponseDTO(address);
	}
	
	public static Address fromAddressDTO(AddressRequestDTO addressDTO) {
		Address address = new Address();
		
		AddressData addressData = new AddressData(addressDTO);
		
		address.setAddressData(addressData);
		
		return address;
	}
	
	public static AddressSnapshot fromAddress(Address address, Rental rental) {
		AddressSnapshot addressSnapshot = new AddressSnapshot(address);
		
		addressSnapshot.setRental(rental);
		
		return addressSnapshot;
	}
}
