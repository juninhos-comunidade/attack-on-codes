package com.attackoncodes.worksync.exception.security;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.attackoncodes.worksync.exception.global.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;
	
	@Override
	public void commence(
			HttpServletRequest request, 
			HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		log.error("Unauthorized error: {}", authException.getMessage());
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		Map<String, String> errors = new LinkedHashMap<>();
	    errors.put("status", String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
	    errors.put("error", "Unauthorized");
	    errors.put("message", authException.getMessage());
	    errors.put("path", request.getServletPath());
        objectMapper.writeValue(response.getOutputStream(), new ErrorResponse(errors));
	}
	
}
