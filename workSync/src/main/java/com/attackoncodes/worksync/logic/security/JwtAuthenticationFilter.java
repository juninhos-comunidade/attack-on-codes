package com.attackoncodes.worksync.logic.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.attackoncodes.worksync.exception.sql.NotFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		return path.equals("/v1/auth/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		String jwt = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);
		}

		if (jwt == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			if (jwtService.isAccessToken(jwt)) {

				final String identifier = jwtService.extractUsername(jwt);

				if (identifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = this.userDetailsService.loadUserByUsername(identifier);

					if (jwtService.isTokenValid(jwt, userDetails)) {
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authToken);
					}
				}
			}
		} catch (NotFoundException | UsernameNotFoundException | ExpiredJwtException e) {
			log.warn("Username not found, or token expired {}", e.getMessage());
		} catch (JwtException e) {
			log.warn("JWT Authentication failed: {}", e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

}
