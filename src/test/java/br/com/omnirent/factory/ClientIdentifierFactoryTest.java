package br.com.omnirent.factory;

import br.com.omnirent.infrastructure.ratelimit.ClientIdentifier;
import br.com.omnirent.infrastructure.ratelimit.ClientIdentifierType;
import br.com.omnirent.utils.Sequence;

public final class ClientIdentifierFactoryTest {

	private ClientIdentifierFactoryTest() {}

	public static ClientIdentifier user() {
		return new ClientIdentifier(
				Sequence.nextString("user-id"), ClientIdentifierType.USER);
	}

	public static ClientIdentifier user(String id) {
		return new ClientIdentifier(id, ClientIdentifierType.USER);
	}

	public static ClientIdentifier ip() {
		return new ClientIdentifier(
				"127.0.0.1", ClientIdentifierType.IP);
	}

	public static ClientIdentifier ip(String ip) {
		return new ClientIdentifier(ip, ClientIdentifierType.IP);
	}

}
