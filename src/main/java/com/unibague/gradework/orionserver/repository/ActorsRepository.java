package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.Actors;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * The ActorsRepository interface provides CRUD operations for the "Actors" collection in MongoDB.
 *
 * This repository extends the MongoRepository interface, which comes with predefined methods
 * for common database operations such as save, find, delete, and more.
 *
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring-managed repository bean.
 *   It allows the repository to be discovered and injected where needed.
 */
@Repository
public interface ActorsRepository extends MongoRepository<Actors, Long> {

}
