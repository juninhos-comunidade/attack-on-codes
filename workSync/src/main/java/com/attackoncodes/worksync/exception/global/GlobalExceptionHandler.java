package com.attackoncodes.worksync.exception.global;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private record ApiErrorResponse(String message) {
	}

	@ExceptionHandler({ GlobalRuntimeException.class })
	public ResponseEntity<ApiErrorResponse> handleCustomExceptions(GlobalRuntimeException ex) {
		var errorResponse = new ApiErrorResponse(ex.getMessage());
		return ResponseEntity.status(ex.getStatus()).body(errorResponse);
	}

	@ExceptionHandler({ GlobalDateTimeException.class })
	public ResponseEntity<ApiErrorResponse> handleGlobalDateTime(GlobalDateTimeException ex) {
		return ResponseEntity.status(ex.getStatus()).body(new ApiErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiErrorResponse("Missing required parameter: " + ex.getParameterName()));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Method not supported: " + ex.getMethod()
				+ ".  The method supported is:" + ex.getSupportedHttpMethods());
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exp) {
		Map<String, String> errors = exp.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
	}

}
