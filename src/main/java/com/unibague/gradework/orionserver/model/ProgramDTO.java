package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProgramDTO {
    private String idProgram;
    private String programName;

    public ProgramDTO(String idProgram, String programName) {
        this.idProgram = idProgram;
        this.programName = programName;
    }
}
