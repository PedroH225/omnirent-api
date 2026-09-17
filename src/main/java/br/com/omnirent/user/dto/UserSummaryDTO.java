package br.com.omnirent.user.dto;

import br.com.omnirent.common.enums.UserStatus;

public record UserSummaryDTO(
		String id,
		String username,
		UserStatus userStatus
	) {}