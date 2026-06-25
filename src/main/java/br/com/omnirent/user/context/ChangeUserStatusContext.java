package br.com.omnirent.user.context;

import br.com.omnirent.common.enums.UserStatus;

public record ChangeUserStatusContext(
		String id,
		UserStatus currentUserStatus,
		String email,
		String username,
		String locale
		) {}
