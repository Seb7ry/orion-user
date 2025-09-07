package com.unibague.gradework.orionuser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

/**
 * Student entity extending User
 * Represents students in the Orion system
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Document(collection = "students")
public class Student extends User {

    @NotBlank(message = "Student ID is required")
    @Pattern(regexp = "^[0-9]{8,12}$", message = "Student ID must be 8-12 digits")
    @Indexed(unique = true)
    private String studentID;

    @NotNull(message = "Status is required")
    @Indexed
    private boolean status;

    @NotBlank(message = "Semester is required")
    @Pattern(regexp = "^(1|2|3|4|5|6|7|8|9|10)$", message = "Semester must be between 1 and 10")
    @Indexed
    private String semester;
}