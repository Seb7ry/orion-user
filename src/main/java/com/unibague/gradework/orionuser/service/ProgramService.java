package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.configuration.ServiceProperties;
import com.unibague.gradework.orionuser.model.ProgramDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Service for communicating with Program Service
 * Uses externalized configuration for service URLs and timeouts
 */
@Slf4j
@Service
public class ProgramService implements IProgramService {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    public ProgramService(RestTemplate restTemplate, ServiceProperties serviceProperties) {
        this.restTemplate = restTemplate;
        this.serviceProperties = serviceProperties;
        log.info("ProgramService initialized with URL: {}", serviceProperties.getProgramServiceUrl());
    }

    @Override
    public List<ProgramDTO> getProgramById(List<String> programIds) {
        if (programIds == null || programIds.isEmpty()) {
            return List.of();
        }

        return programIds.stream()
                .map(this::getProgramDetails)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public ProgramDTO getProgramDetails(String programId) {
        if (programId == null || programId.isBlank()) {
            throw new IllegalArgumentException("Program ID cannot be null or empty");
        }

        String url = serviceProperties.getProgramServiceUrl() + "/" + programId;
        log.debug("Fetching program details for ID: {} from URL: {}", programId, url);

        try {
            Object response = restTemplate
                    .getForEntity(url, Object.class)
                    .getBody();

            if (response instanceof LinkedHashMap<?, ?> programMap) {
                String id = (String) programMap.get("programId");
                String name = (String) programMap.get("programName");

                if (id == null || name == null) {
                    throw new IllegalArgumentException("Retrieved program does not contain valid data");
                }

                log.debug("Successfully retrieved program: {}", name);
                return new ProgramDTO(id, name);
            }

            log.warn("Unexpected response format from Program Service for ID: {}", programId);
            return null;

        } catch (HttpClientErrorException.NotFound e) {
            log.debug("Program not found with ID: {}", programId);
            return null;
        } catch (HttpClientErrorException e) {
            log.error("HTTP error while fetching program {}: {} - {}",
                    programId, e.getStatusCode(), e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Error fetching program with ID {}: {}", programId, e.getMessage(), e);
            return null;
        }
    }
}