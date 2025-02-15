package com.unibague.gradework.orionserver.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The Student class represents a student entity in the system.
 * It extends the User class and includes attributes specific to students.
 *
 * An instance of this class corresponds to a document in the "students" collection in MongoDB.
 *
 * Annotations:
 * - @Data: Automatically generates getters, setters, toString(), equals(), and hashCode().
 * - @RequiredArgsConstructor: Creates a constructor for fields marked as final or @NonNull.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @EqualsAndHashCode(callSuper = true): Ensures equality and hash code generation includes fields from the parent class (User).
 * - @SuperBuilder: Enables a builder pattern that supports inheritance.
 * - @Document: Maps this class to the "students" collection in MongoDB.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Document(collection = "students")
public class Student extends User {

    /**
     * Unique identifier for the student.
     * Used to associate the student with academic records or activities.
     */
    private String studentID;

    /**
     * The enrollment status of the student.
     * True indicates the student is currently enrolled, while false indicates otherwise.
     */
    private boolean status;

    /**
     * The current semester of the student.
     * Example values: "First Semester", "Third Semester".
     */
    private String semester;

    /**
     * The category or type of the student.
     * Example values: "Undergraduate", "Graduate".
     */
    private String category;
}
