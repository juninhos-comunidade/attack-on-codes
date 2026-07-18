package com.attackoncodes.worksync.security.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.attackoncodes.worksync.security.dto.user.PageUserResponse;
import com.attackoncodes.worksync.security.dto.user.UserResponse;
import com.attackoncodes.worksync.security.model.Role;
import com.attackoncodes.worksync.security.repository.filtering.UserSortOption;
import com.attackoncodes.worksync.security.service.UserService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

	private final UserService userService;

	@GetMapping(value = "/me")
	@ResponseStatus(HttpStatus.OK)
	public UserResponse getCurrentUserAuthenticated() {
		return userService.getCurrentUserAuthenticated();
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public UserResponse searchUserById(@PathVariable(value = "id") UUID userId) {
		return userService.searchUserById(userId);
	}

	@GetMapping(value = "/search")
	@ResponseStatus(HttpStatus.OK)
	public PageUserResponse searchUserByParams(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false) String nickname, @RequestParam(required = false) String email,
			@RequestParam(required = false, value = "role") Role roles,
			@RequestParam(required = false, value = "sort") UserSortOption sortParam) {
		return userService.searchUserByParams(page, nickname, email, roles, sortParam);
	}

/*
	@PutMapping(value = "/password")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void changeUserAuthenticatedPassword(@RequestBody PasswordUpdateRequest request,
			HttpServletResponse httpResponse) {
		userService.changeUserPassword(request, httpResponse);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeMyAccount(@RequestBody ConfirmationRequest request, HttpServletResponse httpResponse) {
		userService.removeMyAccount(request, httpResponse);
	}
*/

}
