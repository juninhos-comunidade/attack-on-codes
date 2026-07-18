package com.attackoncodes.worksync.exception.security;

import org.springframework.http.HttpStatus;

import com.attackoncodes.worksync.exception.global.GlobalRuntimeException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class InvalidTokenException extends GlobalRuntimeException {

	public InvalidTokenException(String message) {
		super(message, HttpStatus.FORBIDDEN);
	}

	public InvalidTokenException(String message, Throwable cause) {
		super(message, cause, HttpStatus.FORBIDDEN);
	}

}
