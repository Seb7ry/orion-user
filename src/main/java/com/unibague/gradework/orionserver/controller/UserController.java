package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.*;
import com.unibague.gradework.orionserver.service.RoleService;
import com.unibague.gradework.orionserver.service.IUserService;
import com.unibague.gradework.orionserver.service.ProgramService;
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
 * - {@link RestController}: Marks this class as a RESTful controller.
 * - {@link RequestMapping}: Specifies the base path for all endpoints in this controller (/service/user).
 */
@RestController
@RequestMapping("/service/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;

    /**
     * Validates user role and programs before processing.
     *
     * @param user The user object (Student or Actor) to validate.
     * @return A {@link ResponseEntity} containing an error message if validation fails, or null if validation passes.
     */
    private ResponseEntity<?> validateUser(User user){
        if(user.getRole() == null || user.getRole().getIdRole() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role ID is required in the request body.");
        }

        Optional<Role> role = roleService.getRoleById(user.getRole().getIdRole());
        if(role.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role ID: " + user.getRole().getIdRole());
        }

        user.setRole(role.get());

        if(user.getProgramId() != null && !user.getProgramId().isEmpty()){
            programService.getProgramByIds(user.getProgramId());
        }
        return null;
    }

    /**
     * Creates a new student.
     *
     * @param student The {@link Student} object containing student details.
     * @return A {@link ResponseEntity} with the created student or an error message.
     */
    @PostMapping("/student")
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            ResponseEntity<?> validation = validateUser(student);
            if(validation != null){
                return validation;
            }
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
     * Creates a new actor.
     *
     * @param actor The {@link Actor} object containing actor details.
     * @return A {@link ResponseEntity} with the created actor or an error message.
     */
    @PostMapping("/actor")
    public ResponseEntity<?> createActor(@RequestBody Actor actor) {
        try {
            ResponseEntity<?> validation = validateUser(actor);
            if(validation != null){
                return validation;
            }
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
     * @return A {@link ResponseEntity} containing a list of {@link StudentDTO} objects.
     */
    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudentsDTO() {
        return ResponseEntity.ok(userService.getAllStudentsDTO());
    }

    /**
     * Retrieves all actors.
     *
     * @return A {@link ResponseEntity} containing a list of {@link ActorDTO} objects.
     */
    @GetMapping("/actors")
    public ResponseEntity<List<ActorDTO>> getAllActorsDTO() {
        return ResponseEntity.ok(userService.getAllActorsDTO());
    }

    /**
     * Retrieves a student DTO by ID.
     *
     * @param id The unique identifier of the student.
     * @return A {@link ResponseEntity} containing the {@link StudentDTO} or a 404 status if not found.
     */
    @GetMapping("/student/{id}")
    public ResponseEntity<StudentDTO> getStudentDTOById(@PathVariable String id) {
        return userService.getStudentDTOById(id)
                .map(studentDTO -> new ResponseEntity<>(studentDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves an actor DTO by ID.
     *
     * @param id The unique identifier of the actor.
     * @return A {@link ResponseEntity} containing the {@link ActorDTO} or a 404 status if not found.
     */
    @GetMapping("/actor/{id}")
    public ResponseEntity<ActorDTO> getActorDTOById(@PathVariable String id) {
        return userService.getActorDTOById(id)
                .map(actorDTO -> new ResponseEntity<>(actorDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves a student by their unique identifier.
     *
     * @param id The unique identifier of the student.
     * @return A {@link ResponseEntity} containing the {@link Student} if found, or HTTP status 404 (Not Found) if not.
     */
    @GetMapping("/student/auth/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        return userService.getStudentById(id)
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves an actor by their unique identifier.
     *
     * @param id The unique identifier of the actor.
     * @return A {@link ResponseEntity} containing the {@link Actor} if found, or HTTP status 404 (Not Found) if not.
     */
    @GetMapping("/actor/auth/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable String id) {
        return userService.getActorById(id)
                .map(actor -> new ResponseEntity<>(actor, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves all programs associated with a user.
     *
     * @param userId The unique identifier of the user.
     * @return A {@link ResponseEntity} containing the list of programs or an error message.
     */
    @GetMapping("/{userId}/programs")
    public ResponseEntity<?> getUserPrograms(@PathVariable String userId) {
        try {
            Optional<? extends User> userOpt = userService.getStudentById(userId)
                    .map(student -> (User) student)
                    .or(() -> userService.getActorById(userId).map(actor -> (User) actor));

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
            }

            List<String> programIds = userOpt.get().getProgramId();
            List<ProgramDTO> programs = programService.getProgramByIds(programIds);

            return ResponseEntity.ok(programs.isEmpty() ? List.of() : programs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The email address of the user.
     * @return A {@link ResponseEntity} containing the {@link User} or an error message.
     */
    @GetMapping("/auth/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> userOptional = userService.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with email: " + email);
        }

        return ResponseEntity.ok(userOptional.get());
    }

    /**
     * Updates an existing student's details.
     *
     * @param id             The unique identifier of the student to update.
     * @param studentDetails The updated {@link Student} object containing new values.
     * @return A {@link ResponseEntity} containing the updated {@link Student} object and HTTP status 200 (OK).
     */
    @PutMapping("/student/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        return new ResponseEntity<>(userService.updateStudent(id, studentDetails), HttpStatus.OK);
    }

    /**
     * Updates an existing actor's details.
     *
     * @param id           The unique identifier of the actor to update.
     * @param actorDetails The updated {@link Actor} object containing new values.
     * @return A {@link ResponseEntity} containing the updated {@link Actor} object and HTTP status 200 (OK).
     */
    @PutMapping("/actor/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable String id, @RequestBody Actor actorDetails) {
        return new ResponseEntity<>(userService.updateActor(id, actorDetails), HttpStatus.OK);
    }

    /**
     * Deletes a student by their unique identifier.
     *
     * @param id The unique identifier of the student to delete.
     * @return A {@link ResponseEntity} with HTTP status 204 (No Content) if the deletion is successful.
     */
    @DeleteMapping("/student/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        userService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes an actor by their unique identifier.
     *
     * @param id The unique identifier of the actor to delete.
     * @return A {@link ResponseEntity} with HTTP status 204 (No Content) if the deletion is successful.
     */
    @DeleteMapping("/actor/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable String id) {
        userService.deleteActor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}