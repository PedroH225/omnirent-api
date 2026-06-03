package br.com.omnirent.address;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.address.event.AddressAddedEvent;
import br.com.omnirent.address.event.AddressUpdatedEvent;
import br.com.omnirent.common.enums.DomainEventType;
import br.com.omnirent.common.event.DomainEventPublisher;
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
	
	private DomainEventPublisher eventPublisher;
		
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
		
		AddressResponseDTO result = mapper.toDto(addressRepository.save(address));
		
		eventPublisher.publish(new AddressAddedEvent(
				currentUserProvider.currentUserId(),
				result.getId(),mapper.toAuditSnapshot(result), Instant.now()));
		
		return result;
	}
	
	public AddressResponseDTO updateAddress(AddressRequestDTO addressDTO) {
		Address address = findById(addressDTO.id());
		
		AddressAuditSnapshot oldData = mapper.toAuditSnapshot(address);
		
		address.updateFields(addressDTO);
		
		AddressResponseDTO result = mapper.toDto(addressRepository.save(address));
		
		AddressAuditSnapshot newData = mapper.toAuditSnapshot(result);
		
		eventPublisher.publish(new AddressUpdatedEvent(
				currentUserProvider.currentUserId(),
				addressDTO.id(),oldData,
				newData, Instant.now()));
		
		return result;
	}
	
	public void deleteAddress(String addressId) {
		Address address = findById(addressId);
		addressRepository.delete(address);
	}

}
