package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.Actor;
import com.unibague.gradework.orionserver.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The ActorRepository interface provides CRUD operations for the "Actor" collection in MongoDB.
 *
 * This repository extends the MongoRepository interface, which comes with predefined methods
 * for common database operations such as save, find, delete, and more.
 *
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring-managed repository bean.
 *   It allows the repository to be discovered and injected where needed.
 */
@Repository
public interface ActorRepository extends MongoRepository<Actor, String> {

    /**
     * Retrieves an {@link Actor} entity by its email address.
     *
     * @param email The email address of the actor.
     * @return An {@link Optional} containing the {@link Actor} if found, or empty if no actor exists with the given email.
     */
    Optional<Actor> findByEmail(String email);

    /**
     * Checks whether an Actor with the given email exists in the database.
     *
     * @param email The email address to check.
     * @return True if an Actor with the given email exists, otherwise false.
     */
    boolean existsByEmail(String email);
}
