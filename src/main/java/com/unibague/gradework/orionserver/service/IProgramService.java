package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.ProgramDTO;

import java.util.List;

public interface IProgramService {
    List<ProgramDTO> getProgramByIds(List<String> programIds);
    ProgramDTO fetchProgramDetails(String programId);
}

