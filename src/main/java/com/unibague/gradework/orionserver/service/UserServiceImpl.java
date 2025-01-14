package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.Actors;
import com.unibague.gradework.orionserver.model.Role;
import com.unibague.gradework.orionserver.model.Student;
import com.unibague.gradework.orionserver.model.User;
import com.unibague.gradework.orionserver.repository.ActorsRepository;
import com.unibague.gradework.orionserver.repository.RoleRepository;
import com.unibague.gradework.orionserver.repository.StudentRepository;
import com.unibague.gradework.orionserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The UserServiceImpl class provides the implementation of the UserService interface.
 * It handles the business logic for managing users, including students and actors.
 *
 * Annotations:
 * - @Service: Marks this class as a Spring service, enabling it to be managed by the Spring container.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ActorsRepository actorsRepository;

    /**
     * Creates a new student and assigns a specific role.
     *
     * @param student the Student object containing the student's details.
     * @param roleName the name of the role to assign to the student.
     * @return the created Student object.
     */
    @Override
    public Student createStudent(Student student, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
        student.setRole(role);
        return studentRepository.save(student);
    }

    /**
     * Creates a new actor and assigns a specific role.
     *
     * @param actor the Actors object containing the actor's details.
     * @param roleName the name of the role to assign to the actor.
     * @return the created Actors object.
     */
    @Override
    public Actors createActor(Actors actor, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
        actor.setRole(role);
        return actorsRepository.save(actor);
    }

    /**
     * Retrieves all students.
     *
     * @return a list of Student objects.
     */
    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Retrieves all actors.
     *
     * @return a list of Actors objects.
     */
    @Override
    public List<Actors> getAllActors() {
        return actorsRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the unique identifier of the user.
     * @return an Optional containing the User object if found, or empty if not.
     */
    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(Long.valueOf(id));
    }

    /**
     * Updates an existing student's details.
     *
     * @param id the unique identifier of the student.
     * @param studentDetails the updated Student object.
     * @return the updated Student object.
     */
    @Override
    public Student updateStudent(String id, Student studentDetails) {
        Student existingStudent = studentRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Student with ID " + id + " not found"));

        existingStudent.setFirstName(studentDetails.getFirstName());
        existingStudent.setLastName(studentDetails.getLastName());
        existingStudent.setPhone(studentDetails.getPhone());
        existingStudent.setEmail(studentDetails.getEmail());
        existingStudent.setImage(studentDetails.getImage());
        existingStudent.setSex(studentDetails.getSex());
        existingStudent.setStudentID(studentDetails.getStudentID());
        existingStudent.setStatus(studentDetails.isStatus());
        existingStudent.setSemester(studentDetails.getSemester());
        existingStudent.setCategory(studentDetails.getCategory());

        return studentRepository.save(existingStudent);
    }

    /**
     * Updates an existing actor's details.
     *
     * @param id the unique identifier of the actor.
     * @param actorDetails the updated Actors object.
     * @return the updated Actors object.
     */
    @Override
    public Actors updateActor(String id, Actors actorDetails) {
        Actors existingActor = actorsRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Actor with ID " + id + " not found"));

        existingActor.setFirstName(actorDetails.getFirstName());
        existingActor.setLastName(actorDetails.getLastName());
        existingActor.setPhone(actorDetails.getPhone());
        existingActor.setEmail(actorDetails.getEmail());
        existingActor.setImage(actorDetails.getImage());
        existingActor.setSex(actorDetails.getSex());
        existingActor.setEmployeeId(actorDetails.getEmployeeId());
        existingActor.setPosition(actorDetails.getPosition());

        return actorsRepository.save(existingActor);
    }

    /**
     * Deletes a student by their ID.
     *
     * @param id the unique identifier of the student.
     */
    @Override
    public void deleteStudent(String id) {
        Student existingStudent = studentRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Student with ID " + id + " not found"));
        studentRepository.delete(existingStudent);
    }

    /**
     * Deletes an actor by their ID.
     *
     * @param id the unique identifier of the actor.
     */
    @Override
    public void deleteActor(String id) {
        Actors existingActor = actorsRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Actor with ID " + id + " not found"));
        actorsRepository.delete(existingActor);
    }
}
