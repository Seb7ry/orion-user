package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The RoleRepository interface provides CRUD operations and custom query methods
 * for the "Role" collection in MongoDB.
 *
 * This repository extends the MongoRepository interface, enabling standard database
 * operations as well as a custom method to find roles by their name.
 *
 * Annotations:
 * - @Repository: Marks this interface as a Spring-managed repository bean,
 *   allowing it to be discovered and injected where needed.
 */
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    /**
     * Finds a role by its name.
     *
     * @param name the name of the role to search for.
     * @return an Optional containing the Role object if found, or an empty Optional if not.
     */
    Optional<Role> findByName(String name);
}
