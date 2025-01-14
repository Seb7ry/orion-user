package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.Actors;
import com.unibague.gradework.orionserver.model.Student;
import com.unibague.gradework.orionserver.model.User;

import java.util.List;
import java.util.Optional;

/**
 * The UserService interface defines the business logic for managing users in the system.
 * This includes creating, retrieving, updating, and deleting both Students and Actors.
 */
public interface UserService {

    /**
     * Creates a new student and assigns a specific role to them.
     *
     * @param student the Student object containing the student's details.
     * @param roleName the name of the role to assign to the student.
     * @return the created Student object.
     */
    Student createStudent(Student student, String roleName);

    /**
     * Creates a new actor and assigns a specific role to them.
     *
     * @param actor the Actors object containing the actor's details.
     * @param roleName the name of the role to assign to the actor.
     * @return the created Actors object.
     */
    Actors createActor(Actors actor, String roleName);

    /**
     * Retrieves all students from the database.
     *
     * @return a List of Student objects.
     */
    List<Student> getAllStudents();

    /**
     * Retrieves all actors from the database.
     *
     * @return a List of Actors objects.
     */
    List<Actors> getAllActors();

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user.
     * @return an Optional containing the User object if found, or an empty Optional if not.
     */
    Optional<User> getUserById(String id);

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
     * @param actorDetails an Actors object containing the updated details.
     * @return the updated Actors object.
     */
    Actors updateActor(String id, Actors actorDetails);

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
