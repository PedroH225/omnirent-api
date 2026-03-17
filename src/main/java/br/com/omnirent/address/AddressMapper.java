package br.com.omnirent.address;

import java.util.List;
import java.util.stream.Collectors;

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
		
		address.setStreet(addressDTO.street());
		address.setNumber(addressDTO.number());
		address.setComplement(addressDTO.complement());
		address.setDistrict(addressDTO.district());
		address.setCity(addressDTO.city());
		address.setState(addressDTO.state());
		address.setCountry(addressDTO.country());
		address.setZipCode(addressDTO.zipCode());
		return address;
	}
}
