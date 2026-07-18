package com.attackoncodes.worksync.logic.security;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.attackoncodes.worksync.security.model.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final HttpServletRequest request;

    public User getAuthenticatedUserOrThrow() {
        return getAuthenticatedUser()
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User is not authenticated"));
    }
    
	public String getRequestingClientIp() {
		return this.request.getRemoteAddr();	
	}
	
	public void invalidateUserSession() {
		var session = request.getSession(false);
	    if (session != null) {
	        session.invalidate();
	    }
	    SecurityContextHolder.clearContext();
	}
    
    private Optional<User> getAuthenticatedUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(User.class::isInstance)
                .map(User.class::cast);
    }
    
}
