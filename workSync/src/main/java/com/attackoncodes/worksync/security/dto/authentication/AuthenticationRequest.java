package com.attackoncodes.worksync.security.dto.authentication;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
		
		@NotBlank(message = "Email must not be blank")
		String email,
		
		@NotBlank(message = "Password must not be blank")
		String password
		
		) {

}
