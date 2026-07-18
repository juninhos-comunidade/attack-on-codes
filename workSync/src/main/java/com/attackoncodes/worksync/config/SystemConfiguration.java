package com.attackoncodes.worksync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.ForwardedHeaderFilter;

import com.attackoncodes.worksync.exception.sql.NotFoundException;
import com.attackoncodes.worksync.logic.auditing.ApplicationAuditAware;
import com.attackoncodes.worksync.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") /*, dateTimeProviderRef = "dateTimeProvider" */
public class SystemConfiguration {

	private final UserRepository repository;

	@Bean
	ForwardedHeaderFilter forwardedHeaderFilter() {
		return new ForwardedHeaderFilter();
	}

	@Bean
	UserDetailsService userDetailsService() {
		return identifier -> repository.findByEmail(identifier)
				.orElseThrow(() -> new NotFoundException("User not found with identifier: " + identifier));
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	AuditorAware<String> auditorProvider() {
		return new ApplicationAuditAware();
	}

	/*
	@Bean
	DateTimeProvider dateTimeProvider() {
		return () -> Optional.of(ZonedDateTime.now());
	}
	*/

	@Bean
	PasswordEncoder passwordEncoder() {
		return new Argon2PasswordEncoder(16, // saltLength
				32, // hashLength
				1, // parallelism
				16384, // 64MB memory
				2 // iterations
		);
	}

}
