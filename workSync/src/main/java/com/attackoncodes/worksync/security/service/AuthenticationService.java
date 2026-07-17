package com.attackoncodes.worksync.security.service;

import java.time.Duration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tom.security.hash.exception.security.ActiveSessionException;
import com.tom.security.hash.exception.security.InvalidTokenException;
import com.tom.security.hash.logic.caching.LoginAttemptComponent;
import com.tom.security.hash.logic.security.JwtService;
import com.tom.security.hash.logic.security.SecurityUtils;
import com.tom.security.hash.security.component.CookiesComponent;
import com.tom.security.hash.security.component.LoginHistoryComponent;
import com.tom.security.hash.security.component.TokenComponent;
import com.tom.security.hash.security.component.UserComponent;
import com.tom.security.hash.security.dto.authentication.AuthenticationRequest;
import com.tom.security.hash.security.dto.authentication.AuthenticationResponse;
import com.tom.security.hash.security.dto.authentication.RegisterRequest;
import com.tom.security.hash.security.enums.Role;
import com.tom.security.hash.security.mapper.UserMapper;
import com.tom.security.hash.security.model.User;
import com.tom.security.hash.security.repository.TokenRepository;
import com.tom.security.hash.security.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {

	@Value("${cookies.security.cookie-name}")
	private String refreshTokenCookieName;

	@Value("${security.tokens.refresh-token:7d}")
	private Duration refreshExpiration;

	private final AuthenticationManager authManager;
	private final PasswordEncoder passwordEncoder;
	private final SecurityUtils securityUtils;

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserComponent userComponent;

	private final TokenRepository tokenRepository;
	private final JwtService jwtService;
	private final TokenComponent tokenComponent;

	private final CookiesComponent cookiesComponent;
	private final LoginAttemptComponent loginTryService;
	private final LoginHistoryComponent loginHistory;

	@Transactional
	public AuthenticationResponse register(RegisterRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		var userIp = securityUtils.getRequestingClientIp();
		log.info("IP: {}, is creating user.", userIp);

		checkIfUserIsAlreadyAuthenticated(httpRequest, refreshTokenCookieName);

		userComponent.ensureNicknameAndEmailAreUnique(request.nickname(), request.email());

		if (!Objects.equals(request.password(), request.confirmPassword())) {
			throw new BadCredentialsException("Passwords not matches");
		}

		var user = userMapper.build(request);
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRoles(Role.ALUNO);

		var savedUser = userRepository.save(user);
		var jwtToken = jwtService.generateToken(savedUser);
		var refreshToken = jwtService.generateRefreshToken(savedUser);

		loginHistory.storeLoginHistory(savedUser, userIp);

		tokenComponent.saveUserToken(savedUser, refreshToken);
		cookiesComponent.addCookie(httpResponse, refreshTokenCookieName, refreshToken, refreshExpiration);

		log.info("IP: {}, registered user Email: {}", userIp, savedUser.getEmail());
		return userMapper.toResponse(jwtToken);
	}

	@Transactional
	public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String identifier = request.email();
		var userIp = securityUtils.getRequestingClientIp();
		log.info("IP: {}, is authenticating user: {}", userIp, identifier);

		loginTryService.isLoginBlocked(identifier);
		checkIfUserIsAlreadyAuthenticated(httpRequest, refreshTokenCookieName);

		try {
			var auth = authManager
					.authenticate(new UsernamePasswordAuthenticationToken(identifier, request.password()));

			var user = (User) auth.getPrincipal();
			var jwtToken = jwtService.generateToken(user);
			var refreshToken = jwtService.generateRefreshToken(user);

			tokenComponent.saveUserToken(user, refreshToken);
			cookiesComponent.clearCookie(httpResponse, refreshTokenCookieName);
			cookiesComponent.addCookie(httpResponse, refreshTokenCookieName, refreshToken, refreshExpiration);

			loginTryService.loginSucceeded(identifier);
			loginHistory.storeLoginHistory(user, userIp);
			log.info("IP: {}, authenticated with the user: {}", userIp, user.getUsername());
			return userMapper.toResponse(jwtToken);
		} catch (BadCredentialsException e) {
			log.warn("Failed login attempt for user: {}", request.email());
			loginTryService.loginFailed(identifier);
			throw e;
		}
	}

	@Transactional
	public AuthenticationResponse refreshToken(String providedRefreshToken, HttpServletRequest request,
			HttpServletResponse response) {

		String tokenToProcess = tokenComponent.resolveToken(providedRefreshToken, request);

		if (tokenToProcess == null) {
			throw new InvalidTokenException("No refresh token provided.");
		}

		var storedToken = tokenRepository.findByToken(tokenToProcess)
				.orElseThrow(() -> new InvalidTokenException("Refresh token not found in database."));

		if (storedToken.isExpired() || storedToken.isRevoked()) {
			throw new InvalidTokenException("Refresh token is expired.");
		}

		var user = storedToken.getUser();

		if (!user.isEnabled() || !user.isAccountNonLocked()) {
			log.warn("User banned: {}, tried to authenticate on: {}", user.getUsername(),
					securityUtils.getRequestingClientIp());
			throw new InvalidTokenException("User account is disabled or locked.");
		}

		var newAccessToken = jwtService.generateToken(user);
		var newRefreshToken = jwtService.generateRefreshToken(user);

		storedToken.setRevoked(true);
		storedToken.setExpired(true);

		tokenRepository.save(storedToken);

		tokenComponent.saveUserToken(user, newRefreshToken);
		cookiesComponent.clearCookie(response, refreshTokenCookieName);
		cookiesComponent.addCookie(response, refreshTokenCookieName, newRefreshToken, refreshExpiration);
		return userMapper.toResponse(newAccessToken);
	}

	private void checkIfUserIsAlreadyAuthenticated(HttpServletRequest request, String refreshTokenName) {
		cookiesComponent.getCookieValue(request, refreshTokenName).ifPresent(token -> {
			tokenRepository.findByToken(token).filter(t -> !t.isRevoked() && !t.isExpired()).ifPresent(activeToken -> {
				User existingUser = activeToken.getUser();
				throw new ActiveSessionException(
						"An account (" + existingUser.getUsername() + ") is already logged in. Please log out first.");
			});
		});
	}

}
