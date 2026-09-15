package br.com.omnirent.user.context;

import br.com.omnirent.common.enums.UserStatus;

public record UserFilter(
		String username,
		UserStatus userStatus
	) {}
