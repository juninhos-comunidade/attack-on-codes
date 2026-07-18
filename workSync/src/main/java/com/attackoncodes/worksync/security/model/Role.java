package com.attackoncodes.worksync.security.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

	ANONYMOUS(Collections.emptySet()),

	FUNCIONARIO(Stream.of(Permission.FUNCIONARIO_PERMISSIONS).flatMap(Set::stream).collect(Collectors.toSet())),

	ADMIN(Stream.of(Permission.FUNCIONARIO_PERMISSIONS, Permission.ADMIN_PERMISSIONS).flatMap(Set::stream)
			.collect(Collectors.toSet()));

	@Getter
	private final Set<Permission> permissions;

	private List<SimpleGrantedAuthority> authorities;

	public List<SimpleGrantedAuthority> getAuthorities() {
		if (authorities == null) {
			List<SimpleGrantedAuthority> grantedAuthorities = this.permissions.stream()
					.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
					.collect(Collectors.toList());

			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

			this.authorities = Collections.unmodifiableList(grantedAuthorities);
		}
		return this.authorities;
	}

	public static Role checkValueOf(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return Role.valueOf(value.toUpperCase().trim());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
