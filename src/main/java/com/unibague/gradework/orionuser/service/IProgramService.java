package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.model.ProgramDTO;
import java.util.List;

public interface IProgramService {
    List<ProgramDTO> getProgramById(List<String> programIds);
    ProgramDTO getProgramDetails(String programId);
}
