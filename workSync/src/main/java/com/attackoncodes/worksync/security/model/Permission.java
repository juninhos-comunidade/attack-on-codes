package com.attackoncodes.worksync.security.model;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

	FUNCIONARIO_READ("funcionario:read"),
	FUNCIONARIO_UPDATE("funcionario:update"),
	FUNCIONARIO_DELETE("funcionario:delete"),
	FUNCIONARIO_CREATE("funcionario:create"),

	ADMIN_READ("admin:read"),
	ADMIN_UPDATE("admin:update"),
	ADMIN_DELETE("admin:delete"),
	ADMIN_CREATE("admin:create"),
	;

	@Getter
	private final String permission;

	protected static final Set<Permission> FUNCIONARIO_PERMISSIONS = Set.of(FUNCIONARIO_READ, FUNCIONARIO_UPDATE,
			FUNCIONARIO_DELETE, FUNCIONARIO_CREATE);

	protected static final Set<Permission> ADMIN_PERMISSIONS = Set.of(ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE,
			ADMIN_CREATE);

}