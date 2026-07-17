package com.attackoncodes.worksync.security.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tom.security.hash.exception.sql.NotFoundException;
import com.tom.security.hash.logic.common.ResourceUtils;
import com.tom.security.hash.logic.security.SecurityUtils;
import com.tom.security.hash.security.component.CookiesComponent;
import com.tom.security.hash.security.component.TokenComponent;
import com.tom.security.hash.security.component.UserComponent;
import com.tom.security.hash.security.dto.user.AccountUpdateRequest;
import com.tom.security.hash.security.dto.user.ConfirmationRequest;
import com.tom.security.hash.security.dto.user.PageLoginHistoryResponse;
import com.tom.security.hash.security.dto.user.PageUserResponse;
import com.tom.security.hash.security.dto.user.PasswordUpdateRequest;
import com.tom.security.hash.security.dto.user.UserExport;
import com.tom.security.hash.security.dto.user.UserResponse;
import com.tom.security.hash.security.enums.Role;
import com.tom.security.hash.security.mapper.LoginMapper;
import com.tom.security.hash.security.mapper.UserMapper;
import com.tom.security.hash.security.model.User;
import com.tom.security.hash.security.repository.LoginHistoryRepository;
import com.tom.security.hash.security.repository.UserRepository;
import com.tom.security.hash.security.repository.filtering.UserSortOption;
import com.tom.security.hash.security.repository.filtering.UserSortParameter;
import com.tom.security.hash.security.repository.filtering.UserSpecification;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

	@Value("${application.size.page:20}")
	private int PAGE_SIZE;

	@Value("${cookies.security.cookie-name}")
	private String refreshTokenCookieName;

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserSortParameter userSort;

	private final UserComponent userComponent;
	private final TokenComponent tokenComponent;
	private final CookiesComponent cookiesComponent;

	private final PasswordEncoder passwordEncoder;
	private final SecurityUtils securityUtils;
	private final ResourceUtils resourceUtils;

	private final LoginHistoryRepository loginRepository;
	private final LoginMapper loginMapper;

	@Transactional(readOnly = true)
	public UserResponse getCurrentUserAuthenticated() {
		var user = securityUtils.getAuthenticatedUserOrThrow();
		return userMapper.toResponse(user);
	}

	@Transactional(readOnly = true)
	public UserResponse searchUserById(UUID query) {
		var user = userComponent.findByActiveUser(query);
		return userMapper.toResponse(user);
	}

	@Transactional(readOnly = true)
	public PageLoginHistoryResponse viewCurrentUserLoginHistory(int page) {
		var user = securityUtils.getAuthenticatedUserOrThrow();
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		var loginHistory = loginRepository.findAllByUser(user.getId(), pageable);
		return loginMapper.toResponse(loginHistory);
	}

	@Transactional(readOnly = true)
	public PageUserResponse searchUserByParams(int page, String username, String email, Role roles,
			UserSortOption sortParam) {
		var finalSort = userSort.selectUserSort(sortParam);
		Specification<User> spec = UserSpecification.findByCriteria(username, email, roles);
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, finalSort);
		Page<User> users = userRepository.findAll(spec, pageable);
		return userMapper.toResponse(users);
	}

	@Transactional(readOnly = true)
	public UserExport exportMyUserData() {
		var detachedUser = securityUtils.getAuthenticatedUserOrThrow();
		log.info("IP: {}, user: {}, is requesting their data.", securityUtils.getRequestingClientIp(),
				detachedUser.getUsername());

		var user = userRepository.findById(detachedUser.getId())
				.orElseThrow(() -> new NotFoundException("Authenticated user not found in database."));

		var userData = userMapper.dataExporation(user);
		byte[] data = resourceUtils.bytesToJson(userData);

		String jsonFileName = "user_data_" + user.getUsername() + ".json";
		String zipFileName = "user_data_" + user.getUsername() + ".zip";

		byte[] zipBytes = resourceUtils.resourceToBytes(data, jsonFileName);
		Resource resource = new ByteArrayResource(zipBytes);

		return new UserExport(resource, zipFileName, zipBytes.length);
	}

	@Transactional
	public UserResponse updateUser(AccountUpdateRequest request) {
		var user = securityUtils.getAuthenticatedUserOrThrow();
		log.info("IP: {}, is being edited: {}", securityUtils.getRequestingClientIp(), user.getUsername());
		boolean isSameUsername = request.username().equals(user.getUsername());
		boolean isSameEmail = request.email().equals(user.getEmail());

		if (isSameUsername && isSameEmail) {
			log.info("User {} submitted an update with no data changes. Skipping update.", user.getUsername());
			return userMapper.toResponse(user);
		}

		userComponent.checkIfNicknameAlreadyUsed(user, request.username());
		userComponent.checkIfEmailAlreadyUsed(user, request.email());

		userMapper.update(user, request);
		var userEdited = userRepository.save(user);

		log.info("IP: {}, changed the info of the user: {}, to user: {}", securityUtils.getRequestingClientIp(),
				user.getUsername(), userEdited.getUsername());
		return userMapper.toResponse(userEdited);
	}

	@Transactional
	public void changeUserPassword(PasswordUpdateRequest request, HttpServletResponse httpResponse) {
		var user = securityUtils.getAuthenticatedUserOrThrow();
		var userIp = securityUtils.getRequestingClientIp();
		log.info("IP: {}, is changing password of user: {}", userIp, user.getUsername());

		if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
			throw new BadCredentialsException("Password not matches");
		}

		if (!Objects.equals(request.newPassword(), request.confirmPassword())) {
			throw new BadCredentialsException("Passwords are not the same");
		}

		user.setPassword(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);

		tokenComponent.revokeUserAllTokens(user);
		cookiesComponent.clearCookie(httpResponse, refreshTokenCookieName);
		log.info("IP: {}, changed successfully the password of user: {}", userIp, user.getEmail());
	}

	@Transactional
	public void removeMyAccount(ConfirmationRequest request, HttpServletResponse httpResponse) {
		var user = securityUtils.getAuthenticatedUserOrThrow();
		log.info("IP: {}, is deleting user: {}", securityUtils.getRequestingClientIp(), user.getEmail());

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BadCredentialsException("Incorrect password provided for account deletion.");
		}

		securityUtils.invalidateUserSession();
		tokenComponent.revokeUserAllTokens(user);

		// This will remove the user access and lock account
		// But it won't delete the user from the database.

		user.setAccountNonLocked(false);
		user.setEnabled(false);
		userRepository.save(user);

		// This deletes the user from the db
		// userRepository.deleteById(user.getId());

		tokenComponent.revokeUserAllTokens(user);
		cookiesComponent.clearCookie(httpResponse, refreshTokenCookieName);
		log.info("The user {} has deleted their account", user.getEmail());
	}

}
