package com.unibague.gradework.orionserver.model;

import com.unibague.gradework.orionserver.enumerator.TypeSex;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * The User class represents a generic user entity in the system.
 * It serves as a base class for other user types, such as students or employees.
 *
 * An instance of this class corresponds to a document in the "users" collection in MongoDB.
 *
 * Annotations:
 * - @Data: Automatically generates getters, setters, toString(), equals(), and hashCode().
 * - @RequiredArgsConstructor: Creates a constructor for fields marked as final or @NonNull.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @SuperBuilder: Enables the use of the builder pattern with support for inheritance.
 * - @Document: Maps this class to the "users" collection in MongoDB.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "users")
public class User {

    /**
     * Unique identifier for the user.
     * Annotated with @Id to designate it as the primary key in the MongoDB collection.
     */
    @Id
    private String idUser;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The user's date of birth.
     * Represented as a LocalDate for precise date handling.
     */
    private LocalDate birthDate;

    /**
     * The user's phone number.
     */
    private int phone;

    /**
     * The email address of the user.
     * Used as a primary contact method and for authentication.
     */
    private String email;

    /**
     * URL or path to the user's profile image.
     */
    private String image;

    /**
     * The gender of the user.
     * Represented as an enumerator of type {@link TypeSex}.
     */
    private TypeSex sex;

    /**
     * The password for the user.
     * Stored securely and used for authentication.
     */
    private String password;

    /**
     * The role assigned to the user.
     * Annotated with @DBRef to indicate a reference to a Role document in MongoDB.
     */
    @DBRef
    private Role role;
}