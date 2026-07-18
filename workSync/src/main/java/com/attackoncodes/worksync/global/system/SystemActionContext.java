package com.attackoncodes.worksync.global.system;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SystemActionContext {

	public <T> T runAsSystem(Supplier<T> action) {
		var authorities = List.of(new SimpleGrantedAuthority("ROLE_SYSTEM"));
		var auth = new UsernamePasswordAuthenticationToken("SYSTEM", null, authorities);

		SecurityContextHolder.getContext().setAuthentication(auth);

		try {
			return action.get();
		} finally {
			SecurityContextHolder.clearContext();
		}
	}

}
