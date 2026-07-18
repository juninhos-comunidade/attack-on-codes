package com.attackoncodes.worksync.exception.sql;

import org.springframework.http.HttpStatus;

import com.attackoncodes.worksync.exception.global.GlobalRuntimeException;

@SuppressWarnings("serial")
public class DataViolationException extends GlobalRuntimeException {

	public DataViolationException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

	public DataViolationException(String message, Throwable cause) {
		super(message, cause, HttpStatus.CONFLICT);
	}
}
