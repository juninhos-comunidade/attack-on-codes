package com.attackoncodes.worksync.exception.global;

import java.time.DateTimeException;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public abstract class GlobalDateTimeException extends DateTimeException {

	private final HttpStatus status;

	protected GlobalDateTimeException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	protected GlobalDateTimeException(String message, Throwable cause, HttpStatus status) {
		super(message, cause);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

}
