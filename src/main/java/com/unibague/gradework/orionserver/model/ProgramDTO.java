package com.unibague.gradework.orionserver.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a Program in the system.
 * This class is used to transfer program-related data between different layers of the application.
 */
@Data
@RequiredArgsConstructor
public class ProgramDTO {

    /**
     * Unique identifier for the program.
     */
    private String idProgram;

    /**
     * Name of the program.
     */
    private String programName;

    /**
     * Constructs a {@link ProgramDTO} with the specified program details.
     *
     * @param idProgram   The unique identifier of the program.
     * @param programName The name of the program.
     */
    public ProgramDTO(String idProgram, String programName) {
        this.idProgram = idProgram;
        this.programName = programName;
    }
}
