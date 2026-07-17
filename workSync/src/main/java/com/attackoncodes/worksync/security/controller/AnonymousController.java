package com.attackoncodes.worksync.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tom.security.hash.security.dto.authentication.AuthenticationRequest;
import com.tom.security.hash.security.dto.authentication.AuthenticationResponse;
import com.tom.security.hash.security.dto.authentication.RegisterRequest;
import com.tom.security.hash.security.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AnonymousController {

	private final AuthenticationService authService;

	@PostMapping(value = "/sign-up")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthenticationResponse registerUser(@RequestBody RegisterRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		return authService.register(request, httpRequest, httpResponse);
	}

	@PostMapping(value = "/sign-in")
	@ResponseStatus(HttpStatus.OK)
	public AuthenticationResponse authenticateUser(@RequestBody AuthenticationRequest request,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		return authService.authenticate(request, httpRequest, httpResponse);
	}

	@PostMapping(value = "/refresh-token")
	@ResponseStatus(HttpStatus.OK)
	public AuthenticationResponse refreshToken(
			@CookieValue(name = "${cookies.security.cookie-name}", required = false) String refreshToken,
			HttpServletRequest request, HttpServletResponse response) {
		return authService.refreshToken(refreshToken, request, response);
	}

}
