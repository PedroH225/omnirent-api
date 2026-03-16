package br.com.omnirent.address;

import java.util.List;
import java.util.stream.Collectors;

public class AddressMapper {

	public static List<AddressResponseDTO> toDto(List<Address> addresses) {
		return addresses.stream()
				.map(AddressResponseDTO::new)
				.collect(Collectors.toList());
	}
}
