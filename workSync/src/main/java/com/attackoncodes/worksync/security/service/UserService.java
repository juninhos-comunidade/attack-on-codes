package com.attackoncodes.worksync.security.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attackoncodes.worksync.logic.security.SecurityUtils;
import com.attackoncodes.worksync.security.component.UserComponent;
import com.attackoncodes.worksync.security.dto.user.PageUserResponse;
import com.attackoncodes.worksync.security.dto.user.UserResponse;
import com.attackoncodes.worksync.security.mapper.UserMapper;
import com.attackoncodes.worksync.security.model.Role;
import com.attackoncodes.worksync.security.model.User;
import com.attackoncodes.worksync.security.repository.UserRepository;
import com.attackoncodes.worksync.security.repository.filtering.UserSortOption;
import com.attackoncodes.worksync.security.repository.filtering.UserSortParameter;
import com.attackoncodes.worksync.security.repository.filtering.UserSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

	@Value("${application.page.size:20}")
	private int PAGE_SIZE;

	@Value("${cookies.security.cookie-name}")
	private String refreshTokenCookieName;

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserSortParameter userSort;

	private final UserComponent userComponent;
	
	private final SecurityUtils securityUtils;
	
	/*
	private final TokenComponent tokenComponent;
	private final CookiesComponent cookiesComponent;
	private final PasswordEncoder passwordEncoder;
 	*/

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
	public PageUserResponse searchUserByParams(int page, String username, String email, Role roles,
			UserSortOption sortParam) {
		var finalSort = userSort.selectUserSort(sortParam);
		Specification<User> spec = UserSpecification.findByCriteria(username, email, roles);
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, finalSort);
		Page<User> users = userRepository.findAll(spec, pageable);
		return userMapper.toResponse(users);
	}

	/*
	
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
	 */

}
