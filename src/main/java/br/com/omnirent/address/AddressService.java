package br.com.omnirent.address;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.exception.domain.AddressNotFoundException;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AddressService {

	private AddressRepository addressRepository;
		
	private UserService userService;
	
	private AddressMapper mapper;
	
	public Address findById(String id) {
		Optional<Address> address = addressRepository.findById(id);
		
		if (address.isEmpty()) {
			throw new AddressNotFoundException();
		}
		
		return address.get();
	}
	
	public List<AddressResponseDTO> getUserAddresses(String userId) {
		return addressRepository.findAddressByUser(userId);
	}

	public AddressResponseDTO addAddress(AddressRequestDTO addressDto, String userId) {	
		User user = userService.getUserReference(userId);
				
		Address address = mapper.fromAddressDTO(addressDto);
		
		address.assignUser(user);
				
		return mapper.toDto(addressRepository.save(address));
	}
	
	public AddressResponseDTO updateAddress(AddressRequestDTO addressDTO) {
		Address address = findById(addressDTO.id());
		
		address.updateFields(addressDTO);
		
		return mapper.toDto(addressRepository.save(address));
	}
	
	public void deleteAddress(String addressId) {
		Address address = findById(addressId);
		addressRepository.delete(address);
	}
	
}
