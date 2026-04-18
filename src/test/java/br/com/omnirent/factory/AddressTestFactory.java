package br.com.omnirent.factory;

import java.time.LocalDateTime;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.Sequence;

public final class AddressTestFactory {

    private AddressTestFactory() {}

    public static AddressData defaultAddressData() {
        return new AddressData(
            "Rua Azul", "450", "Apto 12",
            "Centro", "Campinas", "SP",
            "Brazil", "13000-000"
        );
    }

    public static Address forUser(User owner) {
        Address address = new Address();
        address.setAddressData(defaultAddressData());
        address.setUserId(owner.getId());
        return address;
    }
    
    
    public static Address forPersistedUser(User user) {
    	Address address = forUser(user);
    	address.setId(Sequence.nextString("addressId"));
    	address.setCreatedAt(LocalDateTime.now());
    	address.setUpdatedAt(LocalDateTime.now());
    	return address;
    }
    
    public static AddressResponseDTO toAddressDto(Address address) {
		return new AddressResponseDTO(
			    address.getId(),
			    address.getAddressData().getStreet(),
			    address.getAddressData().getNumber(),
			    address.getAddressData().getComplement(),
			    address.getAddressData().getDistrict(),
			    address.getAddressData().getCity(),
			    address.getAddressData().getState(),
			    address.getAddressData().getCountry(),
			    address.getAddressData().getZipCode(),
			    address.getCreatedAt(),
			    address.getUpdatedAt()
			);
	}
    
    public static AddressRequestDTO toRequestDTO(Address address) {
        AddressData ad = address.getAddressData();

        return new AddressRequestDTO(
            address.getId(),
            ad.getStreet(),
            ad.getNumber(),
            ad.getComplement(),
            ad.getDistrict(),
            ad.getCity(),
            ad.getState(),
            ad.getCountry(),
            ad.getZipCode()
        );
    }
}
