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
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Role role = roleService.getRoleById(student.getRole().getIdRole())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + student.getRole().getIdRole()));
        student.setRole(role);

        Student created = userService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/actor")
    public ResponseEntity<Actor> createActor(@RequestBody Actor actor) {
        Role role = roleService.getRoleById(actor.getRole().getIdRole())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + actor.getRole().getIdRole()));
        actor.setRole(role);

        Actor created = userService.createActor(actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
    public ResponseEntity<StudentDTO> getStudentDTOById(@PathVariable String id) {
        return userService.getStudentDTOById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));
    }

    @GetMapping("/actor/{id}")
    public ResponseEntity<ActorDTO> getActorDTOById(@PathVariable String id) {
        return userService.getActorDTOById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found with ID: " + id));
    }

    @GetMapping("/student/auth/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable String id) {
        return userService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));
    }

    @GetMapping("/actor/auth/{id}")
    public ResponseEntity<?> getActorById(@PathVariable String id) {
        return userService.getActorById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found with ID: " + id));
    }

    @GetMapping("/{userId}/programs")
    public ResponseEntity<List<String>> getUserPrograms(@PathVariable String userId) {
        Optional<? extends User> userOpt = userService.getStudentById(userId)
                .map(s -> (User) s)
                .or(() -> userService.getActorById(userId).map(a -> (User) a));

        return userOpt
                .map(user -> ResponseEntity.ok(user.getPrograms()))
                .orElseThrow(() -> new IllegalArgumentException("(Programs) User not found with ID: " + userId));
    }

    @GetMapping("/auth/email/{email}")
    public ResponseEntity<UserLogDTO> getUserByEmail(@PathVariable String email) {
        return userService.findUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @PutMapping("/student/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        Student updated = userService.updateStudent(id, studentDetails);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/actor/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable String id, @RequestBody Actor actorDetails) {
        Actor updated = userService.updateActor(id, actorDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        userService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/actor/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable String id) {
        userService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}