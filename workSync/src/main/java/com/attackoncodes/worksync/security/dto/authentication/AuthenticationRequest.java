package com.attackoncodes.worksync.security.dto.authentication;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
		
		@JsonAlias({"email"}) 
		@NotBlank(message = "Email must not be blank")
		String email,
		
		@NotBlank(message = "Password must not be blank")
		String password
		
		) {

}
