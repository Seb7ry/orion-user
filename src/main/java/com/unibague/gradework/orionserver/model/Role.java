package com.unibague.gradework.orionserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * The Role class represents a role entity in the system.
 * Each role defines a specific set of permissions or responsibilities.
 *
 * An instance of this class corresponds to a document in the "roles" collection in MongoDB.
 *
 * Annotations:
 * - @Data: Generates getters, setters, and other utility methods like toString(), equals(), and hashCode().
 * - @RequiredArgsConstructor: Creates a constructor for fields marked as final or @NonNull.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @Builder: Enables the use of the builder pattern for creating instances of this class.
 * - @Document: Maps this class to the "roles" collection in MongoDB.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "roles")
public class Role {

    /**
     * The unique identifier for the role.
     * Annotated with @Id to specify it as the primary key for the MongoDB document.
     */
    @Id
    private String idRole;

    /**
     * The name of the role.
     * Example values: "Admin", "User", "Moderator".
     */
    private String name;
}
