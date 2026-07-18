package com.attackoncodes.worksync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfigurationSource;

import com.attackoncodes.worksync.exception.security.AuthEntryPointJwt;
import com.attackoncodes.worksync.logic.security.JwtAuthenticationFilter;
import com.attackoncodes.worksync.logic.security.LogoutUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	@Value("${security.front.csrf:false}")
	private boolean enableCsrf;

	private final CorsConfigurationSource corsConfigurationSource;
	private final AuthEntryPointJwt unauthorizedHandler;
	private final JwtAuthenticationFilter jwtFilter;
	private final LogoutUtils logoutUtils;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		if (enableCsrf) {
			http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
		} else {
			http.csrf(AbstractHttpConfigurer::disable);
		}

		http.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

				.logout(logout -> logout
					.logoutUrl("/v1/auth/logout")
					.addLogoutHandler(logoutUtils)
					.logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
				)
				
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/v1/auth/**").permitAll()
					    .requestMatchers("/actuator/**", "/error", "/config").permitAll()
						.anyRequest().authenticated());

		return http.build();
	}

}
