package com.unibague.gradework.orionserver.interfaces;

import java.util.List;

public interface IProgramService {

    Object getProgramId(String programId);
    List<Object> getProgramByIds(List<String> programIds);
}
