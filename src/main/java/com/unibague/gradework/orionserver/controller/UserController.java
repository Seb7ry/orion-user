package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.Actors;
import com.unibague.gradework.orionserver.model.Student;
import com.unibague.gradework.orionserver.model.User;
import com.unibague.gradework.orionserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The UserController class handles HTTP requests related to user management.
 * It provides endpoints to create, retrieve, update, and delete students and actors.
 *
 * Annotations:
 * - @RestController: Marks this class as a RESTful controller.
 * - @RequestMapping: Specifies the base path for all endpoints in this controller (/api/users).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Creates a new student and assigns a specific role.
     *
     * @param student the Student object containing the student's details.
     * @param roleName the name of the role to assign to the student.
     * @return a ResponseEntity containing the created Student object and HTTP status 201 (Created).
     */
    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Student student, @RequestParam String roleName) {
        return new ResponseEntity<>(userService.createStudent(student, roleName), HttpStatus.CREATED);
    }

    /**
     * Creates a new actor and assigns a specific role.
     *
     * @param actor the Actors object containing the actor's details.
     * @param roleName the name of the role to assign to the actor.
     * @return a ResponseEntity containing the created Actors object and HTTP status 201 (Created).
     */
    @PostMapping("/actors")
    public ResponseEntity<Actors> createActor(@RequestBody Actors actor, @RequestParam String roleName) {
        return new ResponseEntity<>(userService.createActor(actor, roleName), HttpStatus.CREATED);
    }

    /**
     * Retrieves all students.
     *
     * @return a ResponseEntity containing a list of all Student objects and HTTP status 200 (OK).
     */
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return new ResponseEntity<>(userService.getAllStudents(), HttpStatus.OK);
    }

    /**
     * Retrieves all actors.
     *
     * @return a ResponseEntity containing a list of all Actors objects and HTTP status 200 (OK).
     */
    @GetMapping("/actors")
    public ResponseEntity<List<Actors>> getAllActors() {
        return new ResponseEntity<>(userService.getAllActors(), HttpStatus.OK);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the unique identifier of the user.
     * @return a ResponseEntity containing the User object and HTTP status 200 (OK) if found,
     *         or HTTP status 404 (Not Found) if the user does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Updates an existing student's details.
     *
     * @param id the unique identifier of the student to update.
     * @param studentDetails the updated Student object.
     * @return a ResponseEntity containing the updated Student object and HTTP status 200 (OK).
     */
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        return new ResponseEntity<>(userService.updateStudent(id, studentDetails), HttpStatus.OK);
    }

    /**
     * Updates an existing actor's details.
     *
     * @param id the unique identifier of the actor to update.
     * @param actorDetails the updated Actors object.
     * @return a ResponseEntity containing the updated Actors object and HTTP status 200 (OK).
     */
    @PutMapping("/actors/{id}")
    public ResponseEntity<Actors> updateActor(@PathVariable String id, @RequestBody Actors actorDetails) {
        return new ResponseEntity<>(userService.updateActor(id, actorDetails), HttpStatus.OK);
    }

    /**
     * Deletes a student by their ID.
     *
     * @param id the unique identifier of the student to delete.
     * @return a ResponseEntity with HTTP status 204 (No Content) if the deletion is successful.
     */
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        userService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes an actor by their ID.
     *
     * @param id the unique identifier of the actor to delete.
     * @return a ResponseEntity with HTTP status 204 (No Content) if the deletion is successful.
     */
    @DeleteMapping("/actors/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable String id) {
        userService.deleteActor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}