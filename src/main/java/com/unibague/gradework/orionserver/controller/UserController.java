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
 * - @RestController: Marks this class as a RESTful controller.
 * - @RequestMapping: Specifies the base path for all endpoints in this controller (/api/users).
 */
@RestController
@RequestMapping("/service/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleServiceImpl;

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
     * @param actor the Actor object containing the actor's details including role ID.
     * @return a ResponseEntity containing the created Actor object and HTTP status 201 (Created).
     */
    @PostMapping("/actors")
    public ResponseEntity<?> createActor(@RequestBody Actor actor) {
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
     * @return a ResponseEntity containing a list of all StudentDTO objects and HTTP status 200 (OK).
     */
    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudentsDTO() {
        return ResponseEntity.ok(userService.getAllStudentsDTO());
    }

    /**
     * Retrieves all actors.
     *
     * @return a ResponseEntity containing a list of all ActorDTO objects and HTTP status 200 (OK).
     */
    @GetMapping("/actors")
    public ResponseEntity<List<ActorDTO>> getAllActorsDTO() {
        return ResponseEntity.ok(userService.getAllActorsDTO());
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<StudentDTO> getStudentDTOById(@PathVariable String id) {
        return userService.getStudentDTOById(id)
                .map(studentDTO -> new ResponseEntity<>(studentDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/actor/{id}")
    public ResponseEntity<ActorDTO> getActorDTOById(@PathVariable String id) {
        return userService.getActorDTOById(id)
                .map(actorDTO -> new ResponseEntity<>(actorDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/student/auth/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        return userService.getStudentById(id)
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/actor/auth/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable String id) {
        return userService.getActorById(id)
                .map(actor -> new ResponseEntity<>(actor, HttpStatus.OK))
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
            Optional<Student> studentOpt = userService.getStudentById(userId);
            Optional<Actor> actorOpt = userService.getActorById(userId);

            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                List<String> programIds = student.getProgramId();
                List<ProgramDTO> programs = programService.getProgramByIds(programIds);

                return ResponseEntity.ok(programs.isEmpty() ? List.of() : programs);
            }

            if (actorOpt.isPresent()) {
                Actor actor = actorOpt.get();
                List<String> programIds = actor.getProgramId();
                List<ProgramDTO> programs = programService.getProgramByIds(programIds);

                return ResponseEntity.ok(programs.isEmpty() ? List.of() : programs);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

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
     * @param actorDetails the updated Actor object.
     * @return a ResponseEntity containing the updated Actor object and HTTP status 200 (OK).
     */
    @PutMapping("/actors/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable String id, @RequestBody Actor actorDetails) {
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