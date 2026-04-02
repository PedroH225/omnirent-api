package br.com.omnirent.address;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.exception.domain.AddressNotFoundException;
import br.com.omnirent.user.User;
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
			throw new AddressNotFoundException();
		}
		
		return address.get();
	}
	
	public List<AddressResponseDTO> getUserAddresses(String userId) {
		List<Address> userAddresses = userService.findById(userId).getAddresses();
		
		return AddressMapper.toDto(userAddresses);
	}

	public AddressResponseDTO addAddress(AddressRequestDTO addressDto, String userId) {
		User user = userService.findById(userId);
		
		Address address = AddressMapper.fromAddressDTO(addressDto);
		
		address.addUser(user);
				
		return AddressMapper.toDto(addressRepository.save(address));
	}
	
	public AddressResponseDTO updateAddress(AddressRequestDTO addressDTO) {
		Address address = findById(addressDTO.id());
		
		address.updateFields(addressDTO);
		
		return AddressMapper.toDto(addressRepository.save(address));
	}
	
	public void deleteAddress(String addressId) {
		Address address = findById(addressId);
		addressRepository.delete(address);
	}
	
}
