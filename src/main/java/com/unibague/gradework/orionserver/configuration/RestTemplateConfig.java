package com.unibague.gradework.orionserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for defining application-wide beans.
 *
 * This class is responsible for creating and providing a {@link RestTemplate} bean,
 * which is used for making HTTP requests to external services within the application.
 *
 * Annotations:
 * - {@link Configuration}: Marks this class as a configuration class, allowing Spring
 *   to detect and register its beans.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and registers a {@link RestTemplate} bean.
     *
     * The {@link RestTemplate} is a Spring utility class used to perform HTTP
     * requests and communicate with external APIs or microservices.
     *
     * @return a new instance of {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
