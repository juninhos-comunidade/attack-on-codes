package com.attackoncodes.worksync.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Value("${settings.cors.allowed-origins}")
    private String allowedOrigins;
	
	@Value("${settings.cors.max-age:3600}")
	private Long corsTime;
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    
        config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        
	    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
	    config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
	    
	    config.setAllowCredentials(true);
	    config.setMaxAge(corsTime);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}
	
}
