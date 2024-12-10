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
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Crear un estudiante
    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Student student, @RequestParam String roleName) {
        return new ResponseEntity<>(userService.createStudent(student, roleName), HttpStatus.CREATED);
    }

    @PostMapping("/actors")
    public ResponseEntity<Actors> createActor(@RequestBody Actors actor, @RequestParam String roleName) {
        return new ResponseEntity<>(userService.createActor(actor, roleName), HttpStatus.CREATED);
    }

    // Obtener todos los estudiantes
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return new ResponseEntity<>(userService.getAllStudents(), HttpStatus.OK);
    }

    // Obtener todos los actores
    @GetMapping("/actors")
    public ResponseEntity<List<Actors>> getAllActors() {
        return new ResponseEntity<>(userService.getAllActors(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Actualizar un estudiante
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        return new ResponseEntity<>(userService.updateStudent(id, studentDetails), HttpStatus.OK);
    }

    // Actualizar un actor
    @PutMapping("/actors/{id}")
    public ResponseEntity<Actors> updateActor(@PathVariable String id, @RequestBody Actors actorDetails) {
        return new ResponseEntity<>(userService.updateActor(id, actorDetails), HttpStatus.OK);
    }

    // Eliminar un estudiante
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        userService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Eliminar un actor
    @DeleteMapping("/actors/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable String id) {
        userService.deleteActor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
