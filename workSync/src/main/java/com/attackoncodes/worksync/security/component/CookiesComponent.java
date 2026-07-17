package com.attackoncodes.worksync.security.component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CookiesComponent {

    @Value("${cookies.security.same-site:Lax}")
    private String sameSiteMode;

    @Value("${cookies.security.secure:true}")
    private boolean secure;

    public void addCookie(HttpServletResponse response, String name, String value, Duration duration) {
		boolean isSecure = this.secure;
        if ("None".equalsIgnoreCase(sameSiteMode)) {
            isSecure = true;
        }
		
		ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)    // Prevents access from JavaScript (XSS protection)
                .secure(isSecure)      // Only sent over HTTPS
                .path("/")         // Available to the entire site
                .maxAge(duration) 
                .sameSite(sameSiteMode)   // OR Strict
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
	
	public Optional<String> getCookieValue(HttpServletRequest request, String name) {
	    if (request.getCookies() == null) {
	        return Optional.empty();
	    }
		
	    return Arrays.stream(request.getCookies())
	            .filter(cookie -> name.equals(cookie.getName()))
	            .map(Cookie::getValue)
	            .findFirst();
	}
	
	public void clearCookie(HttpServletResponse response, String name) {
	    ResponseCookie cookie = ResponseCookie.from(name, "")
	    		.httpOnly(true)
	    		.secure(secure)
	    		.sameSite(sameSiteMode)
	    		.path("/")
	            .maxAge(0)
	            .build();
	    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
	
}
