package com.attackoncodes.worksync.security.dto.user;

import com.attackoncodes.worksync.security.model.Role;

public record UserResponse(
		
		String nickname,
		String email,
		Role roles
		
) {

}
