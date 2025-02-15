package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The StudentRepository interface provides CRUD operations for the "Student" collection in MongoDB.
 *
 * This repository extends the MongoRepository interface, which comes with predefined methods
 * for common database operations, such as saving, finding, and deleting Student documents.
 *
 * Annotations:
 * - @Repository: Marks this interface as a Spring-managed repository bean.
 *   This allows it to be discovered and injected where needed in the application.
 */
@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByEmail(String email);
}
