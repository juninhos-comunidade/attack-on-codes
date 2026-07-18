package com.attackoncodes.worksync.security.dto.user;

import com.attackoncodes.worksync.global.constraints.UserConstraints;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(
		
		@Size(min = 4, max = UserConstraints.NICKNAME_MAX_LENGTH, message = "Username must be at least 4 characters and limited to: " + UserConstraints.NICKNAME_MAX_LENGTH)
	    String username,

	    @Email(message = "Email must be valid")
		@Size(min = 4, max = UserConstraints.NICKNAME_MAX_LENGTH, message = "Mail size is limited to: " + UserConstraints.EMAIL_MAX_LENGTH)
	    String email

		) {

}
