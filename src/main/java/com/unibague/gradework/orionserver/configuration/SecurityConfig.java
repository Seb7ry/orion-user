package com.unibague.gradework.orionserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The SecurityConfig class configures the security settings for the application.
 *
 * This configuration uses Spring Security to define how requests are authenticated and authorized.
 * It provides a basic security setup that allows all requests without authentication
 * and disables CSRF protection.
 *
 * Annotations:
 * - @Configuration: Marks this class as a source of bean definitions.
 * - @EnableWebSecurity: Enables Spring Security's web security support.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     *
     * - All HTTP requests are allowed without authentication.
     * - CSRF protection is disabled for simplicity.
     *   This is not recommended in production environments unless handled explicitly elsewhere.
     *
     * @param http the HttpSecurity object to customize security settings.
     * @return a SecurityFilterChain instance configured with the defined settings.
     * @throws Exception if an error occurs while building the security configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permits all incoming requests without authentication.
                )
                .csrf(csrf -> csrf.disable()); // Disables Cross-Site Request Forgery (CSRF) protection.

        return http.build();
    }
}