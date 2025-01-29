package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.interfaces.IProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Service class responsible for handling program-related operations.
 *
 * This service interacts with the external "Program" microservice to retrieve
 * program information based on provided program IDs.
 *
 * Annotations:
 * - @Service: Marks this class as a Spring service, allowing it to be managed as a Spring Bean.
 */
@Service
public class ProgramService implements IProgramService {

    /**
     * RestTemplate is used to make HTTP requests to the external "Program" microservice.
     */    @Autowired
    private RestTemplate restTemplate;

    /**
     * Base URL of the "Program" microservice.
     * This URL is used to fetch program details via HTTP requests.
     */
    private static final String PROGRAM_SERVICE_URL="http://localhost:8090/service/program";

    /**
     * Retrieves a program by its unique identifier.
     *
     * This method makes an HTTP GET request to the "Program" microservice to fetch
     * details of a specific program using its ID.
     *
     * @param programId The unique identifier of the program.
     * @return The program object retrieved from the microservice.
     */
    @Override
    public Object getProgramId(String programId) {
        return restTemplate.getForEntity(PROGRAM_SERVICE_URL + "/" + programId, Object.class).getBody();
    }

    /**
     * Retrieves a list of programs based on their unique identifiers.
     *
     * This method makes multiple HTTP GET requests to the "Program" microservice
     * to fetch details of all specified program IDs.
     *
     * @param programIds A list of program IDs to fetch details for.
     * @return A list of program objects retrieved from the microservice.
     */
    @Override
    public List<Object> getProgramByIds(List<String> programIds) {
        return programIds.stream()
                .map(id -> restTemplate.getForEntity(PROGRAM_SERVICE_URL + "/" + id, Object.class).getBody())
                .toList();
    }
}
