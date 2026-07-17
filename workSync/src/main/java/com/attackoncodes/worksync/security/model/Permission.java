package com.attackoncodes.worksync.security.model;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

	USER_READ("user:read"),
	USER_UPDATE("user:update"),
	USER_DELETE("user:delete"),
	USER_CREATE("user:create"),

	ADMIN_READ("admin:read"),
	ADMIN_UPDATE("admin:update"),
	ADMIN_DELETE("admin:delete"),
	ADMIN_CREATE("admin:create"),
	;

	@Getter
	private final String permission;

	protected static final Set<Permission> USER_PERMISSIONS = Set.of(
            USER_READ, USER_UPDATE, USER_DELETE, USER_CREATE
    );

    protected static final Set<Permission> ADMIN_PERMISSIONS = Set.of(
            ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_CREATE
    );

}