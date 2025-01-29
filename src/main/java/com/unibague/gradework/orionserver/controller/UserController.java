package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.Actors;
import com.unibague.gradework.orionserver.model.Role;
import com.unibague.gradework.orionserver.model.Student;
import com.unibague.gradework.orionserver.model.User;
import com.unibague.gradework.orionserver.interfaces.UserService;
import com.unibague.gradework.orionserver.service.ProgramService;
import com.unibague.gradework.orionserver.service.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * The UserController class handles HTTP requests related to user management.
 * It provides endpoints to create, retrieve, update, and delete students and actors.
 *
 * Annotations:
 * - @RestController: Marks this class as a RESTful controller.
 * - @RequestMapping: Specifies the base path for all endpoints in this controller (/api/users).
 */
@RestController
@RequestMapping("/service/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programService;
    @Autowired
    private RoleServiceImpl roleServiceImpl;

    /**
     * Creates a new student, assigns a specific role, and optionally associates programs with the student.
     *
     * @param student the Student object containing the student's details including role ID.
     * @return a ResponseEntity containing the created Student object and HTTP status 201 (Created).
     */
    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            if (student.getProgramId() != null && !student.getProgramId().isEmpty()) {
                programService.getProgramByIds(student.getProgramId());
            }

            if (student.getRole() == null || student.getRole().getIdRole() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Role ID is required in the request body.");
            }

            Optional<Role> role = roleServiceImpl.getRoleById(student.getRole().getIdRole());
            if (role.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid role ID: " + student.getRole().getIdRole());
            }

            student.setRole(role.get());

            return new ResponseEntity<>(userService.createStudent(student), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid program ID(s): " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Creates a new actor, assigns a specific role, and optionally associates programs with the actor.
     *
     * @param actor the Actors object containing the actor's details including role ID.
     * @return a ResponseEntity containing the created Actors object and HTTP status 201 (Created).
     */
    @PostMapping("/actors")
    public ResponseEntity<?> createActor(@RequestBody Actors actor) {
        try {
            if (actor.getProgramId() != null && !actor.getProgramId().isEmpty()) {
                programService.getProgramByIds(actor.getProgramId());
            }

            if (actor.getRole() == null || actor.getRole().getIdRole() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Role ID is required in the request body.");
            }

            Optional<Role> role = roleServiceImpl.getRoleById(actor.getRole().getIdRole());
            if (role.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid role ID: " + actor.getRole().getIdRole());
            }

            actor.setRole(role.get());

            return new ResponseEntity<>(userService.createActor(actor), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid program ID(s): " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
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
     * Retrieves all programs associated with a specific user.
     *
     * @param userId the unique identifier of the user.
     * @return a ResponseEntity containing the list of programs and HTTP status 200 (OK) or an error message.
     */
    @GetMapping("/{userId}/programs")
    public ResponseEntity<?> getUserPrograms(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            return ResponseEntity.ok(programService.getProgramByIds(user.getProgramId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
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