package br.com.omnirent.address.context;

import br.com.omnirent.common.audit.AuditBody;

public record AddressAuditSnapshot(
        String id,
        String street,
        String number,
        String complement,
        String district,
        String city,
        String state,
        String country,
        String zipCode
		) implements AuditBody {}