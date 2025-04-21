package com.unibague.gradework.orionserver.controller;

import com.unibague.gradework.orionserver.model.*;
import com.unibague.gradework.orionserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service/user")
public class UserController {

    @Autowired private IUserService userService;
    @Autowired private IRoleService roleService;

    @PostMapping("/student")
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            Role role = roleService.getRoleById(student.getRole().getIdRole())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + student.getRole().getIdRole()));
            student.setRole(role);

            Student created = userService.createStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/actor")
    public ResponseEntity<?> createActor(@RequestBody Actor actor) {
        try {
            Role role = roleService.getRoleById(actor.getRole().getIdRole())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + actor.getRole().getIdRole()));
            actor.setRole(role);

            Actor created = userService.createActor(actor);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudentsDTO() {
        return ResponseEntity.ok(userService.getAllStudentsDTO());
    }

    @GetMapping("/actors")
    public ResponseEntity<List<ActorDTO>> getAllActorsDTO() {
        return ResponseEntity.ok(userService.getAllActorsDTO());
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<?> getStudentDTOById(@PathVariable String id) {
        return userService.getStudentDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/actor/{id}")
    public ResponseEntity<?> getActorDTOById(@PathVariable String id) {
        return userService.getActorDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/student/auth/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable String id) {
        return userService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/actor/auth/{id}")
    public ResponseEntity<?> getActorById(@PathVariable String id) {
        return userService.getActorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{userId}/programs")
    public ResponseEntity<?> getUserPrograms(@PathVariable String userId) {
        Optional<? extends User> userOpt = userService.getStudentById(userId)
                .map(s -> (User) s)
                .or(() -> userService.getActorById(userId).map(a -> (User) a));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
        }

        return ResponseEntity.ok(userOpt.get().getPrograms());
    }

    @GetMapping("/auth/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userService.findUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/student/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        try {
            return ResponseEntity.ok(userService.updateStudent(id, studentDetails));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/actor/{id}")
    public ResponseEntity<?> updateActor(@PathVariable String id, @RequestBody Actor actorDetails) {
        try {
            return ResponseEntity.ok(userService.updateActor(id, actorDetails));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable String id) {
        try {
            userService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found with ID: " + id);
        }
    }

    @DeleteMapping("/actor/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable String id) {
        try {
            userService.deleteActor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actor not found with ID: " + id);
        }
    }
}