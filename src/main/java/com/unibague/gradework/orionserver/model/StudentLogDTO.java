package com.unibague.gradework.orionserver.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class StudentLogDTO extends UserLogDTO {
    private String studentID;
    private boolean status;
    private String semester;
}
