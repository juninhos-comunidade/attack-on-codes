package com.attackoncodes.worksync.security.dto.authentication;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tom.security.hash.global.constraints.UserConstraints;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		
		@JsonAlias( {
				"username",
				"nickname" })
	    @NotBlank(message = "Nickname must not be blank")
		@Size(min = UserConstraints.NICKNAME_MIN_LENGTH, max = UserConstraints.NICKNAME_MAX_LENGTH, message = "Nickname must be between "
				+ UserConstraints.NICKNAME_MIN_LENGTH + " and " + UserConstraints.NICKNAME_MAX_LENGTH
				+ " characters")
		String nickname,
		
		@NotBlank(message = "Email must not be blank")
		@Email(message = "Please provide a valid email address")
		@Size(min = UserConstraints.EMAIL_MIN_LENGTH, max = UserConstraints.EMAIL_MAX_LENGTH, message = "Email must be between "
				+ UserConstraints.EMAIL_MIN_LENGTH + " and " + UserConstraints.EMAIL_MAX_LENGTH
				+ " characters")
		String email,
		
	    @NotBlank(message = "Password must not be blank")
		@Size(min = UserConstraints.MINIMAL_PASSWORD_SIZE, message = "Password must be at least "
				+ UserConstraints.MINIMAL_PASSWORD_SIZE
				+ " characters")
		String password,
		
		@JsonAlias({ "passwordConfirmation",
				"confirmPassword" })
	    @NotBlank(message = "Confirm password must not be blank")		
		String confirmPassword
		
		) {

}
