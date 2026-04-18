package br.com.omnirent.factory;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.user.domain.User;

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
}
