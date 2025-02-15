package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.*;
import com.unibague.gradework.orionserver.repository.ActorRepository;
import com.unibague.gradework.orionserver.repository.RoleRepository;
import com.unibague.gradework.orionserver.repository.StudentRepository;
import com.unibague.gradework.orionserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * The UserService class provides the implementation of the IUserService interface.
 * It handles the business logic for managing users, including students and actors.
 *
 * Annotations:
 * - @Service: Marks this class as a Spring service, enabling it to be managed by the Spring container.
 */
@Service
public class UserService implements IUserService {

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ActorRepository actorsRepository;

    @Override
    public Student createStudent(Student student) {
        if (student.getRole() == null || student.getRole().getIdRole() == null || student.getRole().getIdRole().isEmpty()) {
            throw new RuntimeException("Role ID is required");
        }

        Role role = roleRepository.findById(student.getRole().getIdRole())
                .orElseThrow(() -> new RuntimeException("Role with ID " + student.getRole().getIdRole() + " not found"));

        student.setRole(role);
        return studentRepository.save(student);
    }

    @Override
    public Actor createActor(Actor actor) {
        if (actor.getRole() == null || actor.getRole().getIdRole() == null || actor.getRole().getIdRole().isEmpty()) {
            throw new RuntimeException("Role ID is required");
        }

        Role role = roleRepository.findById(actor.getRole().getIdRole())
                .orElseThrow(() -> new RuntimeException("Role with ID " + actor.getRole().getIdRole() + " not found"));

        actor.setRole(role);
        return actorsRepository.save(actor);
    }

    /**
     * Retrieves all students.
     *
     * @return a list of StudentDTO objects.
     */
    @Override
    public List<StudentDTO> getAllStudentsDTO() {
        List<Student> students = studentRepository.findAll();

        return students.stream().map(student -> {
            List<ProgramDTO> programList = student.getProgramId().stream()
                    .map(programService::fetchProgramDetails)
                    .filter(Objects::nonNull)
                    .toList();

            return new StudentDTO(
                    student.getIdUser(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getBirthDate(),
                    student.getPhone(),
                    student.getEmail(),
                    student.getImage(),
                    student.getSex().name(),
                    student.getRole().getName(),
                    programList,
                    student.getStudentID(),
                    student.isStatus(),
                    student.getSemester(),
                    student.getCategory()
            );
        }).toList();
    }

    /**
     * Retrieves all actors.
     *
     * @return a list of ActorDTO objects.
     */
    @Override
    public List<ActorDTO> getAllActorsDTO() {
        List<Actor> actors = actorsRepository.findAll();

        return actors.stream().map(actor -> {
            List<ProgramDTO> programList = actor.getProgramId().stream()
                    .map(programService::fetchProgramDetails)
                    .filter(Objects::nonNull)
                    .toList();

            return new ActorDTO(
                    actor.getIdUser(),
                    actor.getFirstName(),
                    actor.getLastName(),
                    actor.getBirthDate(),
                    actor.getPhone(),
                    actor.getEmail(),
                    actor.getImage(),
                    actor.getSex().name(),
                    actor.getRole().getName(),
                    programList,
                    actor.getEmployeeId(),
                    actor.getPosition()
            );
        }).toList();
    }

    /**
     * Retrieves a studentDTO by their ID.
     *
     * @param id the unique identifier of the user.
     * @return an Optional containing the StudentDTO object if found, or empty if not.
     */
    @Override
    public Optional<StudentDTO> getStudentDTOById(String id) {
        return studentRepository.findById(id)
                .map(student -> {
                    List<ProgramDTO> programDTOList = student.getProgramId().stream()
                            .map(programService::fetchProgramDetails)
                            .filter(Objects::nonNull)
                            .toList();

                    return new StudentDTO(
                            student.getIdUser(),
                            student.getFirstName(),
                            student.getLastName(),
                            student.getBirthDate(),
                            student.getPhone(),
                            student.getEmail(),
                            student.getImage(),
                            student.getSex().name(),
                            student.getRole().getName(),
                            programDTOList,
                            student.getStudentID(),
                            student.isStatus(),
                            student.getSemester(),
                            student.getCategory()
                    );
                });
    }

    /**
     * Retrieves an ActorDTO by their ID.
     *
     * @param id the unique identifier of the user.
     * @return an Optional containing the ActorDTO object if found, or empty if not.
     */
    @Override
    public Optional<ActorDTO> getActorDTOById(String id) {
        return actorsRepository.findById(id)
                .map(actor -> {
                    List<ProgramDTO> programDTOList = actor.getProgramId().stream()
                            .map(programService::fetchProgramDetails)
                            .filter(Objects::nonNull)
                            .toList();

                    return new ActorDTO(
                            actor.getIdUser(),
                            actor.getFirstName(),
                            actor.getLastName(),
                            actor.getBirthDate(),
                            actor.getPhone(),
                            actor.getEmail(),
                            actor.getImage(),
                            actor.getSex().name(),
                            actor.getRole().getName(),
                            programDTOList,
                            actor.getEmployeeId(),
                            actor.getPosition()
                    );
                });
    }

    /**
     * Retrieves a student by their ID.
     *
     * @param id the unique identifier of the user.
     * @return an Optional containing the Student object if found, or empty if not.
     */
    @Override
    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    /**
     * Retrieves an actor by their ID.
     *
     * @param id the unique identifier of the user.
     * @return an Optional containing the Actor object if found, or empty if not.
     */
    @Override
    public Optional<Actor> getActorById(String id) {
        return actorsRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return studentRepository.findByEmail(email)
                .map(student -> (User)student)
                .or(() -> actorsRepository.findByEmail(email)
                        .map(actor -> (User)actor));
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
        Student existingStudent = studentRepository.findById(id)
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
     * @param actorDetails the updated Actor object.
     * @return the updated Actor object.
     */
    @Override
    public Actor updateActor(String id, Actor actorDetails) {
        Actor existingActor = actorsRepository.findById(id)
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
        Student existingStudent = studentRepository.findById(id)
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
        Actor existingActor = actorsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actor with ID " + id + " not found"));
        actorsRepository.delete(existingActor);
    }
}
