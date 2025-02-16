package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing users in the system.
 * It provides methods to create, retrieve, update, and delete both Students and Actors.
 */
public interface IUserService {

    /**
     * Creates a new student and assigns them a specific role.
     *
     * @param student The {@link Student} object containing the student's details.
     * @return The created {@link Student} object.
     */
    Student createStudent(Student student);

    /**
     * Creates a new actor and assigns them a specific role.
     *
     * @param actor The {@link Actor} object containing the actor's details.
     * @return The created {@link Actor} object.
     */
    Actor createActor(Actor actor);

    /**
     * Retrieves a list of all students from the system.
     *
     * @return A {@link List} of {@link StudentDTO} objects representing all students.
     */
    List<StudentDTO> getAllStudentsDTO();

    /**
     * Retrieves a list of all actors from the system.
     *
     * @return A {@link List} of {@link ActorDTO} objects representing all actors.
     */
    List<ActorDTO> getAllActorsDTO();

    /**
     * Retrieves a student as a DTO by their unique identifier.
     *
     * @param id The unique identifier of the student.
     * @return An {@link Optional} containing the {@link StudentDTO} if found, or an empty {@link Optional} if not.
     */
    Optional<StudentDTO> getStudentDTOById(String id);

    /**
     * Retrieves an actor as a DTO by their unique identifier.
     *
     * @param id The unique identifier of the actor.
     * @return An {@link Optional} containing the {@link ActorDTO} if found, or an empty {@link Optional} if not.
     */
    Optional<ActorDTO> getActorDTOById(String id);

    /**
     * Retrieves a student by their unique identifier.
     *
     * @param id The unique identifier of the student.
     * @return An {@link Optional} containing the {@link Student} if found, or an empty {@link Optional} if not.
     */
    Optional<Student> getStudentById(String id);

    /**
     * Retrieves an actor by their unique identifier.
     *
     * @param id The unique identifier of the actor.
     * @return An {@link Optional} containing the {@link Actor} if found, or an empty {@link Optional} if not.
     */
    Optional<Actor> getActorById(String id);

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user.
     * @return An {@link Optional} containing the {@link User} if found, or an empty {@link Optional} if not.
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Updates an existing student's information based on their unique identifier.
     *
     * @param id The unique identifier of the student to update.
     * @param studentDetails A {@link Student} object containing the updated details.
     * @return The updated {@link Student} object.
     */
    Student updateStudent(String id, Student studentDetails);

    /**
     * Updates an existing actor's information based on their unique identifier.
     *
     * @param id The unique identifier of the actor to update.
     * @param actorDetails An {@link Actor} object containing the updated details.
     * @return The updated {@link Actor} object.
     */
    Actor updateActor(String id, Actor actorDetails);

    /**
     * Deletes a student from the system based on their unique identifier.
     *
     * @param id The unique identifier of the student to delete.
     */
    void deleteStudent(String id);

    /**
     * Deletes an actor from the system based on their unique identifier.
     *
     * @param id The unique identifier of the actor to delete.
     */
    void deleteActor(String id);
}
