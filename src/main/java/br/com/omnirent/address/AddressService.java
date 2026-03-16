package br.com.omnirent.address;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.user.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AddressService {

	private AddressRepository addressRepository;
	
	private UserService userService;
	
	public Address findById(String id) {
		Optional<Address> address = addressRepository.findById(id);
		
		if (address.isEmpty()) {
			throw new RuntimeException("Address not found.");
		}
		
		return address.get();
	}
	
	public List<AddressResponseDTO> getUserAddresses(String userId) {
		List<Address> userAddresses = userService.findById(userId).getAddresses();
		
		return AddressMapper.toDto(userAddresses);
	}
}
