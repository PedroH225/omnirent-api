package br.com.omnirent.address;

public record AddressRequestDTO( 
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
