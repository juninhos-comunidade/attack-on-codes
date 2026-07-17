package com.attackoncodes.worksync.security.controller;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tom.security.hash.security.dto.user.ConfirmationRequest;
import com.tom.security.hash.security.dto.user.PageLoginHistoryResponse;
import com.tom.security.hash.security.dto.user.PageUserResponse;
import com.tom.security.hash.security.dto.user.PasswordUpdateRequest;
import com.tom.security.hash.security.dto.user.UserResponse;
import com.tom.security.hash.security.enums.Role;
import com.tom.security.hash.security.repository.filtering.UserSortOption;
import com.tom.security.hash.security.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
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

	@GetMapping(value = "/history")
	@ResponseStatus(HttpStatus.OK)
	public PageLoginHistoryResponse getCurrentLoginHistory(
			@RequestParam(defaultValue = "0", required = false) int page) {
		return userService.viewCurrentUserLoginHistory(page);
	}

	@GetMapping(value = "/export", produces = "application/zip")
	@ResponseStatus(HttpStatus.OK)
	public Resource exportUserInfo(HttpServletResponse httpResponse) {
		var export = userService.exportMyUserData();

		String headerValue = ContentDisposition.attachment().filename(export.filename()).build().toString();
		httpResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, headerValue);

		return export.resource();
	}

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

}
