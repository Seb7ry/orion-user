package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * The UserRepository interface provides CRUD operations for the "User" collection in MongoDB.
 *
 * This repository extends the MongoRepository interface, enabling standard database
 * operations, such as saving, updating, retrieving, and deleting User documents.
 *
 * Annotations:
 * - @Repository: Marks this interface as a Spring-managed repository bean,
 *   allowing it to be discovered and injected where required.
 */
@Repository
public interface UserRepository extends MongoRepository<User, Long> {
}
