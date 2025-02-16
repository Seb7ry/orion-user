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
 * Implementation of the {@link IUserService} interface that provides user management functionalities,
 * including operations for students and actors.
 */
@Service
public class UserService implements IUserService {

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ActorRepository actorsRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Validates whether the given role exists in the database.
     *
     * @param role The role to be validated.
     * @return The existing {@link Role} entity.
     * @throws IllegalArgumentException if the role ID is null, empty, or does not exist.
     */
    protected Role validateRole(Role role) {
        if(role == null || role.getIdRole() == null || role.getIdRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role id is required.");
        }
        return roleRepository.findById(role.getIdRole())
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + role.getIdRole() + " not found"));
    }

    /**
     * Validates whether an email is unique in the system.
     *
     * @param email The email to be validated.
     * @throws IllegalArgumentException if the email is null, empty, or already exists in the database.
     */
    protected void validateEmail(String email) {
        if(email == null || email.trim().isEmpty()){
            throw new IllegalArgumentException("Email is required.");
        }

        boolean emailExists = studentRepository.existsByEmail(email) || actorsRepository.existsByEmail(email);
        if(emailExists){
           throw new IllegalArgumentException("A user with email " + email + " already exists.");
        }
    }

    /**
     * Creates a new student with the specified role after validation.
     *
     * @param student The student to be created.
     * @return The newly created {@link Student}.
     */
    @Override
    public Student createStudent(Student student) {
        validateEmail(student.getEmail());
        Role role = validateRole(student.getRole());

        student.setRole(role);
        return studentRepository.save(student);
    }

    /**
     * Creates a new actor with the specified role after validation.
     *
     * @param actor The actor to be created.
     * @return The newly created {@link Actor}.
     */
    @Override
    public Actor createActor(Actor actor) {
        validateEmail(actor.getEmail());
        Role role = validateRole(actor.getRole());

        actor.setRole(role);
        return actorsRepository.save(actor);
    }

    /**
     * Retrieves all students and converts them into DTOs.
     *
     * @return A list of {@link StudentDTO} objects.
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
     * Retrieves all actors and converts them into DTOs.
     *
     * @return A list of {@link ActorDTO} objects.
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
     * Retrieves a student DTO by its ID.
     *
     * @param id The student's unique identifier.
     * @return An {@link Optional} containing the {@link StudentDTO} if found.
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
     * Retrieves an actor DTO by its ID.
     *
     * @param id The actor's unique identifier.
     * @return An {@link Optional} containing the {@link ActorDTO} if found.
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
     * Retrieves a student by their unique identifier.
     *
     * @param id The unique identifier of the student.
     * @return An {@link Optional} containing the {@link Student} if found, or an empty {@link Optional} if not.
     */
    @Override
    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    /**
     * Retrieves an actor by their unique identifier.
     *
     * @param id The unique identifier of the actor.
     * @return An {@link Optional} containing the {@link Actor} if found, or an empty {@link Optional} if not.
     */
    @Override
    public Optional<Actor> getActorById(String id) {
        return actorsRepository.findById(id);
    }

    /**
     * Finds a user (either a Student or an Actor) by their email address.
     *
     * @param email The email address of the user.
     * @return An {@link Optional} containing the {@link User} if found, or an empty {@link Optional} if not.
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return studentRepository.findByEmail(email)
                .map(student -> (User)student)
                .or(() -> actorsRepository.findByEmail(email)
                        .map(actor -> (User)actor));
    }

    /**
     * Validates whether an email is unique when updating a user.
     *
     * @param existingEmail The current email of the user.
     * @param newEmail The new email being updated.
     * @throws IllegalArgumentException if the new email is already in use by another user.
     */
    private void validateEmailOnUpdate(String existingEmail, String newEmail) {
        if (!existingEmail.equals(newEmail)) {
            boolean emailExistsInStudents = studentRepository.existsByEmail(newEmail);
            boolean emailExistsInActors = actorsRepository.existsByEmail(newEmail);

            if (emailExistsInStudents || emailExistsInActors) {
                throw new IllegalArgumentException("The email " + newEmail + " is already in use by another user.");
            }
        }
    }

    /**
     * Updates an existing student's information.
     *
     * @param id The unique identifier of the student to update.
     * @param studentDetails A {@link Student} object containing the updated details.
     * @return The updated {@link Student} object.
     * @throws RuntimeException if the student with the given ID does not exist.
     */
    @Override
    public Student updateStudent(String id, Student studentDetails) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student with ID " + id + " not found"));

        validateEmailOnUpdate(existingStudent.getEmail(), studentDetails.getEmail());

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
     * Updates an existing actor's information.
     *
     * @param id The unique identifier of the actor to update.
     * @param actorDetails An {@link Actor} object containing the updated details.
     * @return The updated {@link Actor} object.
     * @throws RuntimeException if the actor with the given ID does not exist.
     */
    @Override
    public Actor updateActor(String id, Actor actorDetails) {
        Actor existingActor = actorsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actor with ID " + id + " not found"));

        validateEmailOnUpdate(existingActor.getEmail(), actorDetails.getEmail());

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
     * Deletes a student from the system based on their unique identifier.
     *
     * @param id The unique identifier of the student to delete.
     * @throws RuntimeException if the student with the given ID does not exist.
     */
    @Override
    public void deleteStudent(String id) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student with ID " + id + " not found"));
        studentRepository.delete(existingStudent);
    }

    /**
     * Deletes an actor from the system based on their unique identifier.
     *
     * @param id The unique identifier of the actor to delete.
     * @throws RuntimeException if the actor with the given ID does not exist.
     */
    @Override
    public void deleteActor(String id) {
        Actor existingActor = actorsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actor with ID " + id + " not found"));
        actorsRepository.delete(existingActor);
    }
}
