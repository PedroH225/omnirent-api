package br.com.omnirent.address;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.AddressErrorType;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AddressService {

	private AddressRepository addressRepository;
		
	private UserService userService;
	
	private AddressMapper mapper;
	
	private CurrentUserProvider currentUserProvider;
	
	public Address findById(String id) {
		return addressRepository.findById(id)
				.orElseThrow(() -> new ApiException(AddressErrorType.NOT_FOUND));
	}
	
	public Address getValidReference(String addressId, String userId) {
		boolean found = addressRepository.verifyAddress(addressId, userId);
		if (!found) {
			throw new ApiException(AddressErrorType.NOT_FOUND);
		}
		return addressRepository.getReferenceById(addressId);
	}
	
	public List<AddressResponseDTO> getUserAddresses() {
		String userId = currentUserProvider.currentUserId();

		return addressRepository.findAddressByUser(userId);
	}

	public AddressResponseDTO addAddress(AddressRequestDTO addressDto) {	
		String userId = currentUserProvider.currentUserId();
		userService.requireExistence(userId);
				
		Address address = mapper.fromAddressDTO(addressDto, userId);
		
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
