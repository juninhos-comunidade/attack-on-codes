package com.attackoncodes.worksync.exception.security;

import org.springframework.http.HttpStatus;

import com.attackoncodes.worksync.exception.global.GlobalRuntimeException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class ActiveSessionException extends GlobalRuntimeException {
	public ActiveSessionException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

	public ActiveSessionException(String message, Throwable cause) {
		super(message, cause, HttpStatus.CONFLICT);
	}
}
