package com.unibague.gradework.orionserver.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The Actor class represents an employee entity in the system.
 * It extends the User class and includes additional attributes specific to employees.
 *
 * An instance of this class corresponds to a document in the "actors" collection in MongoDB.
 *
 * Annotations:
 * - @Data: Generates getters, setters, and other utility methods like toString() and hashCode().
 * - @RequiredArgsConstructor: Creates a constructor with required (final or @NonNull) fields.
 * - @AllArgsConstructor: Creates a constructor with all fields.
 * - @EqualsAndHashCode(callSuper = true): Ensures equality and hash code generation includes fields from the parent class (User).
 * - @SuperBuilder: Supports building objects with inheritance.
 * - @Document: Specifies the MongoDB collection name ("actors").
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Document(collection = "actors")
public class Actor extends User {

    /**
     * Unique identifier for the employee.
     * Used to associate the employee with specific records or activities.
     */
    private String employeeId;

    /**
     * The position or job title of the employee within the organization.
     * Example values: "Manager", "Administrator", "Technician".
     */
    private String position;
}
