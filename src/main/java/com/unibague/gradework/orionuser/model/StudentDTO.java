package com.unibague.gradework.orionuser.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class StudentDTO extends UserDTO {
    private String studentID;
    private boolean status;
    private String semester;
}
