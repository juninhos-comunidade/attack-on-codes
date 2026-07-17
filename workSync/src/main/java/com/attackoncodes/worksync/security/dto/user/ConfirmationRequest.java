package com.attackoncodes.worksync.security.dto.user;

import jakarta.validation.constraints.NotBlank;

public record ConfirmationRequest(
		
	    @NotBlank(message = "Password must not be blank")
		String password
		
) {

}
