package br.com.omnirent.address.context;

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
) {}