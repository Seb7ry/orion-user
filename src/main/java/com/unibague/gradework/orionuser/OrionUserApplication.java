package com.unibague.gradework.orionuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Orion User Service
 * Entry point for the User Management microservice
 */
@SpringBootApplication
public class OrionUserApplication {

    /**
     * Main method to start the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(OrionUserApplication.class, args);
    }
}