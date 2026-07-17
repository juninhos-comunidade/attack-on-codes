package com.attackoncodes.worksync.security.dto.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tom.security.hash.global.constraints.UserConstraints;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequest(
		
		@JsonAlias( {
				"currentPassword",
				"password" })
	    @NotBlank(message = "Current password must not be blank")
	    String currentPassword,

	    @NotBlank(message = "New password must not be blank")
		@Size(min = UserConstraints.MINIMAL_PASSWORD_SIZE, message = "Password must be at least "
				+ UserConstraints.MINIMAL_PASSWORD_SIZE
				+ " characters")
	    String newPassword,

		@JsonAlias({ "passwordConfirmation",
				"confirmPassword" })
	    @NotBlank(message = "Confirmation password must not be blank")
	    String confirmPassword
		
		) {

}
