package br.com.omnirent.infrastructure.ratelimit;

public record ClientIdentifier(
	    String identifier,
	    ClientIdentifierType type
	) {}
