package com.unibague.gradework.orionuser.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Role entity for the Orion system
 * Contains role information and associated permissions
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "roles")
public class Role {

    @Id
    private String idRole;

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    @Indexed
    private String name;

    /**
     * List of permissions associated with this role
     * Each permission is a string identifier (e.g., "USER_CREATE", "STUDENT_READ", "DOCUMENT_APPROVE")
     */
    @Builder.Default
    private List<String> permisos = new ArrayList<>();
}