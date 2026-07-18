package com.attackoncodes.worksync.logic.auditing;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.attackoncodes.worksync.security.model.User;

public class ApplicationAuditAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			return Optional.empty();
		}

		if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM"))) {
			return Optional.of("SYSTEM");
		}

		if (auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
			Object principal = auth.getPrincipal();
			if (principal instanceof User user) {
				return Optional.ofNullable(user.getNickname());
			}
			return Optional.of("UNKNOWN_USER");
		}

		return Optional.empty();
	}

}
