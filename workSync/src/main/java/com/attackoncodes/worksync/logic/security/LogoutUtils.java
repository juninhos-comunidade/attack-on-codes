package com.attackoncodes.worksync.logic.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.attackoncodes.worksync.security.component.CookiesComponent;
import com.attackoncodes.worksync.security.component.TokenComponent;
import com.attackoncodes.worksync.security.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutUtils implements LogoutHandler {

	@Value("${cookies.security.cookie-name:_ratkin_}")
	private String refreshTokenCookieName;

	private final TokenComponent tokenComponent;
	private final CookiesComponent cookiesComponent;
	private final SecurityUtils securityUtils;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		cookiesComponent.clearCookie(response, refreshTokenCookieName);
		if (authentication != null && authentication.getPrincipal() instanceof User) {
			var user = (User) authentication.getPrincipal();
			tokenComponent.revokeUserAllTokens(user);
			securityUtils.invalidateUserSession();
		}
	}
}
