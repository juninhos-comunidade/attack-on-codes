package com.attackoncodes.worksync.security.dto.user;

import java.time.ZonedDateTime;

import com.tom.security.hash.security.enums.Role;

public record UserResponse(
		
		String nickname,
		String email,
		Role roles,
		ZonedDateTime lastLogin,
		LoginHistoryResponse historyResponse
		
) {

}
