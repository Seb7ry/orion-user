package com.unibague.gradework.orionserver.repository;

import com.unibague.gradework.orionserver.model.Actor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends MongoRepository<Actor, String> {

    Optional<Actor> findByEmail(String email);

    boolean existsByEmail(String email);
}
