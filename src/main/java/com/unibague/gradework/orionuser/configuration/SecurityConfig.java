// src/main/java/com/unibague/gradework/orionuser/security/SecurityConfig.java
package com.unibague.gradework.orionuser.configuration;

import com.unibague.gradework.orionuser.security.InternalServiceAuthenticationFilter;
import com.unibague.gradework.orionuser.security.ServiceAuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(ServiceAuthProperties.class)
public class SecurityConfig {

  private final InternalServiceAuthenticationFilter internalFilter;

  public SecurityConfig(InternalServiceAuthenticationFilter internalFilter) {
    this.internalFilter = internalFilter;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
          // Endpoints internos a los que entra orion-auth (ajusta paths si usas otros):
          .requestMatchers("/api/roles/**", "/api/users/**", "/api/programs/**").authenticated()
          // salud/públicos
          .requestMatchers("/actuator/**").permitAll()
          // el resto según tu necesidad
          .anyRequest().permitAll()
      )
      .httpBasic(Customizer.withDefaults());

    // muy importante: registrar el filtro ANTES del AnonymousAuthenticationFilter
    http.addFilterBefore(internalFilter, AnonymousAuthenticationFilter.class);

    return http.build();
  }
}
