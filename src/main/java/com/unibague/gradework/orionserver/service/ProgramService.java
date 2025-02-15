package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.ProgramDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

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
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Base URL of the "Program" microservice.
     * This URL is used to fetch program details via HTTP requests.
     */
    private static final String PROGRAM_SERVICE_URL="http://localhost:8090/service/program";

    /**
     * Retrieves a program by its unique identifier and converts it into a ProgramDTO.
     *
     * @param programId The unique identifier of the program.
     * @return A ProgramDTO containing the program details, or null if not found.
     */
    @Override
    public ProgramDTO fetchProgramDetails(String programId) {
        System.out.println("Fetching program details for ID: " + programId);
        try {
            Object programData = restTemplate.getForEntity(PROGRAM_SERVICE_URL + "/" + programId, Object.class).getBody();

            if (programData instanceof LinkedHashMap<?, ?> programMap) {
                System.out.println("Program Data: " + programMap);
                return new ProgramDTO(
                        (String) programMap.get("programId"),
                        (String) programMap.get("programName"));
            }
        } catch (Exception e) {
            System.out.println("Error fetching program details for ID " + programId + ": " + e.getMessage());
        }

        System.out.println("Program not found for ID: " + programId);
        return null;
    }

    /**
     * Retrieves a list of programs based on their unique identifiers.
     *
     * @param programIds A list of program IDs to fetch details for.
     * @return A list of ProgramDTO objects.
     */
    @Override
    public List<ProgramDTO> getProgramByIds(List<String> programIds) {
        return programIds.stream()
                .map(this::fetchProgramDetails)  // Ahora usamos solo `fetchProgramDetails`
                .filter(Objects::nonNull)
                .toList();
    }
}
