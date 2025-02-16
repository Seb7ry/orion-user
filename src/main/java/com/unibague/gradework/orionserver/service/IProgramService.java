package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.ProgramDTO;

import java.util.List;

/**
 * Service interface for handling program-related operations.
 */
public interface IProgramService {

    /**
     * Retrieves a list of programs based on the provided program IDs.
     *
     * @param programIds List of program IDs to fetch.
     * @return List of {@link ProgramDTO} objects containing program details.
     */
    List<ProgramDTO> getProgramByIds(List<String> programIds);

    /**
     * Fetches detailed information of a specific program based on its ID.
     *
     * @param programId The ID of the program to retrieve.
     * @return A {@link ProgramDTO} object containing the program details.
     */
    ProgramDTO fetchProgramDetails(String programId);
}

