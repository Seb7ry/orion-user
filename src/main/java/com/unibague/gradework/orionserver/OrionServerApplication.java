package com.unibague.gradework.orionserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OrionServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrionServerApplication.class, args);
    }

}
