package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.*;

import java.util.List;
import java.util.Optional;

/**
 * The IUserService interface defines the business logic for managing users in the system.
 * This includes creating, retrieving, updating, and deleting both Students and Actor.
 */
public interface IUserService {

    /**
     * Creates a new student and assigns a specific role to them.
     *
     * @param student the Student object containing the student's details.
     * @return the created Student object.
     */
    Student createStudent(Student student);

    /**
     * Creates a new actor and assigns a specific role to them.
     *
     * @param actor the Actor object containing the actor's details.
     * @return the created Actor object.
     */
    Actor createActor(Actor actor);

    /**
     * Retrieves all students from the database.
     *
     * @return a List of Student objects.
     */
    List<StudentDTO> getAllStudentsDTO();

    /**
     * Retrieves all actors from the database.
     *
     * @return a List of Actor objects.
     */
    List<ActorDTO> getAllActorsDTO();

    /**
     * Retrieves a studentDTO by their unique identifier.
     *
     * @param id the unique identifier of the student.
     * @return an Optional containing the StudentDTO object if found, or an empty Optional if not.
     */
    Optional<StudentDTO> getStudentDTOById(String id);

    /**
     * Retrieves an actorDTO by their unique identifier.
     *
     * @param id the unique identifier of the actor.
     * @return an Optional containing the ActorDTO object if found, or an empty Optional if not.
     */
    Optional<ActorDTO> getActorDTOById(String id);

    /**
     * Retrieves a student by their unique identifier.
     *
     * @param id the unique identifier of the student.
     * @return an Optional containing the Student object if found, or an empty Optional if not.
     */
    Optional<Student> getStudentById(String id);

    /**
     * Retrieves an actor by their unique identifier.
     *
     * @param id the unique identifier of the actor.
     * @return an Optional containing the Actor object if found, or an empty Optional if not.
     */
    Optional<Actor> getActorById(String id);

    Optional<User> findUserByEmail(String email);

    /**
     * Updates an existing student by their unique identifier.
     *
     * @param id the unique identifier of the student to update.
     * @param studentDetails a Student object containing the updated details.
     * @return the updated Student object.
     */
    Student updateStudent(String id, Student studentDetails);

    /**
     * Updates an existing actor by their unique identifier.
     *
     * @param id the unique identifier of the actor to update.
     * @param actorDetails an Actor object containing the updated details.
     * @return the updated Actor object.
     */
    Actor updateActor(String id, Actor actorDetails);

    /**
     * Deletes a student by their unique identifier.
     *
     * @param id the unique identifier of the student to delete.
     */
    void deleteStudent(String id);

    /**
     * Deletes an actor by their unique identifier.
     *
     * @param id the unique identifier of the actor to delete.
     */
    void deleteActor(String id);
}
