package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.ProgramDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Service
public class ProgramService implements IProgramService {

    private static final String PROGRAM_SERVICE_URL = "http://localhost:8093/service/program";
    private final RestTemplate restTemplate;

    public ProgramService (RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ProgramDTO> getProgramById(List<String> programIds) {
        if(programIds == null || programIds.isEmpty()) {
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
            throw new IllegalArgumentException("El ID del programa no puede estar vacío.");
        }

        try {
            Object response = restTemplate
                    .getForEntity(PROGRAM_SERVICE_URL + "/" + programId, Object.class)
                    .getBody();

            if (response instanceof LinkedHashMap<?, ?> programMap) {
                String id = (String) programMap.get("programId");
                String name = (String) programMap.get("programName");

                if (id == null || name == null) {
                    throw new IllegalArgumentException("El programa recuperado no contiene datos válidos.");
                }

                return new ProgramDTO(id, name);
            }
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Programa no encontrado con ID: " + programId);
        } catch (Exception e) {
            System.out.println("Error al obtener el programa con ID " + programId + ": " + e.getMessage());
        }

        return null;
    }
}
