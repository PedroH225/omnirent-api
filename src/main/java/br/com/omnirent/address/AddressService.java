package br.com.omnirent.address;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AddressService {

	private AddressRepository addressRepository;
	
	public Address findById(String id) {
		Optional<Address> address = addressRepository.findById(id);
		
		if (address.isEmpty()) {
			throw new RuntimeException("Address not found.");
		}
		
		return address.get();
	}
}
