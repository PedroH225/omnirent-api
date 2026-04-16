package br.com.omnirent.address;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.omnirent.address.context.AddressInfo;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.address.domain.AddressSnapshot;
import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.rental.domain.Rental;

@Component
public class AddressMapper {

	public List<AddressResponseDTO> toDto(List<Address> addresses) {
		return addresses.stream()
				.map(a -> toDto(a))
				.collect(Collectors.toList());
	}
	
	public AddressResponseDTO toDto(Address address) {
	    AddressData addressData = address.getAddressData();

	    return new AddressResponseDTO(
	            address.getId(), addressData.getStreet(), addressData.getNumber(),
	            addressData.getComplement(), addressData.getDistrict(), addressData.getCity(),
	            addressData.getState(), addressData.getCountry(), addressData.getZipCode(),
	            address.getCreatedAt(), address.getUpdatedAt()
	    );
	}
	
	public AddressSnapshotDTO toSnapDto(AddressSnapshot address) {
		AddressData addressData = address.getAddressData();
	    return new AddressSnapshotDTO(
	            address.getId(), addressData.getStreet(), addressData.getNumber(),
	            addressData.getComplement(), addressData.getDistrict(), addressData.getCity(),
	            addressData.getState(), addressData.getCountry(), addressData.getZipCode()
	    );
	}
	
	public Address fromAddressDTO(AddressRequestDTO addressDTO, String userId) {
		Address address = new Address();
		
		AddressData addressData = new AddressData(addressDTO);
		
		address.setUserId(userId);
		
		address.setAddressData(addressData);
		
		return address;
	}
	
	public AddressSnapshot fromRentContext(AddressInfo address, Rental rental) {
	    AddressSnapshot addressSnapshot = new AddressSnapshot(
	            address.getStreet(), address.getNumber(),
	            address.getComplement(), address.getDistrict(), address.getCity(),
	            address.getState(), address.getCountry(), address.getZipCode(),
	            rental);		
		addressSnapshot.setRental(rental);
		
		return addressSnapshot;
	}
}
