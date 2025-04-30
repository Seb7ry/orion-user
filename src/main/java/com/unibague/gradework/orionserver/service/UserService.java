package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.*;
import com.unibague.gradework.orionserver.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final IProgramService programService;
    private final StudentRepository studentRepository;
    private final ActorRepository actorsRepository;
    private final PasswordEncoder passwordEncoder;
    private final IValidationService validationService;

    public UserService(IProgramService programService,
                       StudentRepository studentRepository,
                       ActorRepository actorsRepository,
                       PasswordEncoder passwordEncoder,
                       IValidationService validationService) {
        this.programService = programService;
        this.studentRepository = studentRepository;
        this.actorsRepository = actorsRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    @Override
    public Student createStudent(Student student) {
        validationService.validateIdUser(student.getIdUser());
        validationService.validateEmail(student.getEmail());

        if (!student.isStatus()) throw new IllegalArgumentException("Status is not valid");

        student.setRole(validationService.validateRole(student.getRole()));
        student.setPassword(passwordEncoder.encode(student.getPassword()));

        if (student.getPrograms() != null && !student.getPrograms().isEmpty()) {
            student.setPrograms(student.getPrograms());
        } else {
            student.setPrograms(List.of());
        }

        return studentRepository.save(student);
    }

    @Override
    public Actor createActor(Actor actor) {
        validationService.validateIdUser(actor.getIdUser());
        validationService.validateEmail(actor.getEmail());

        actor.setRole(validationService.validateRole(actor.getRole()));
        actor.setPassword(passwordEncoder.encode(actor.getPassword()));

        if (actor.getPrograms() != null && !actor.getPrograms().isEmpty()) {
            actor.setPrograms(actor.getPrograms());
        } else {
            actor.setPrograms(List.of());
        }

        return actorsRepository.save(actor);
    }

    @Override
    public List<StudentDTO> getAllStudentsDTO() {
        return studentRepository.findAll().stream().map(student -> {
            List<ProgramDTO> programs = programService.getProgramById(
                    Optional.ofNullable(student.getPrograms())
                            .orElse(List.of())
            );
            return StudentDTO.builder()
                    .idUser(student.getIdUser())
                    .name(student.getName())
                    .phone(student.getPhone())
                    .email(student.getEmail())
                    .image(student.getImage())
                    .sex(student.getSex())
                    .role(student.getRole())
                    .programs(programs)
                    .studentID(student.getStudentID())
                    .status(student.isStatus())
                    .semester(student.getSemester())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ActorDTO> getAllActorsDTO() {
        return actorsRepository.findAll().stream().map(actor -> {
            List<ProgramDTO> programs = programService.getProgramById(
                    Optional.ofNullable(actor.getPrograms())
                            .orElse(List.of())
            );

            return ActorDTO.builder()
                    .idUser(actor.getIdUser())
                    .name(actor.getName())
                    .phone(actor.getPhone())
                    .email(actor.getEmail())
                    .image(actor.getImage())
                    .sex(actor.getSex())
                    .role(actor.getRole())
                    .programs(programs)
                    .position(actor.getPosition())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<StudentDTO> getStudentDTOById(String id) {
        return studentRepository.findById(id).map(student -> {
            List<ProgramDTO> programs = programService.getProgramById(
                    Optional.ofNullable(student.getPrograms())
                            .orElse(List.of())
            );

            return StudentDTO.builder()
                    .idUser(student.getIdUser())
                    .name(student.getName())
                    .phone(student.getPhone())
                    .email(student.getEmail())
                    .image(student.getImage())
                    .sex(student.getSex())
                    .role(student.getRole())
                    .programs(programs)
                    .studentID(student.getStudentID())
                    .status(student.isStatus())
                    .semester(student.getSemester())
                    .build();
        });
    }

    @Override
    public Optional<ActorDTO> getActorDTOById(String id) {
        return actorsRepository.findById(id).map(actor -> {
            List<ProgramDTO> programs = programService.getProgramById(
                    Optional.ofNullable(actor.getPrograms())
                            .orElse(List.of())
            );

            return ActorDTO.builder()
                    .idUser(actor.getIdUser())
                    .name(actor.getName())
                    .phone(actor.getPhone())
                    .email(actor.getEmail())
                    .image(actor.getImage())
                    .sex(actor.getSex())
                    .role(actor.getRole())
                    .programs(programs)
                    .position(actor.getPosition())
                    .build();
        });
    }

    @Override
    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Actor> getActorById(String id) {
        return actorsRepository.findById(id);
    }

    @Override
    public Optional<UserLogDTO> findUserByEmail(String email) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            List<ProgramDTO> programDetails = programService.getProgramById(
                    Optional.ofNullable(student.getPrograms()).orElse(List.of())
            );

            StudentLogDTO result = StudentLogDTO.builder()
                    .idUser(student.getIdUser())
                    .name(student.getName())
                    .email(student.getEmail())
                    .phone(student.getPhone())
                    .image(student.getImage())
                    .sex(student.getSex())
                    .role(student.getRole())
                    .password(student.getPassword())
                    .programs(programDetails)
                    .studentID(student.getStudentID())
                    .status(student.isStatus())
                    .semester(student.getSemester())
                    .build();

            return Optional.of(result);
        }

        Optional<Actor> actorOpt = actorsRepository.findByEmail(email);
        if (actorOpt.isPresent()) {
            Actor actor = actorOpt.get();
            List<ProgramDTO> programDetails = programService.getProgramById(
                    Optional.ofNullable(actor.getPrograms()).orElse(List.of())
            );

            ActorLogDTO result = ActorLogDTO.builder()
                    .idUser(actor.getIdUser())
                    .name(actor.getName())
                    .email(actor.getEmail())
                    .phone(actor.getPhone())
                    .image(actor.getImage())
                    .sex(actor.getSex())
                    .role(actor.getRole())
                    .password(actor.getPassword())
                    .programs(programDetails)
                    .position(actor.getPosition())
                    .build();

            return Optional.of(result);
        }

        return Optional.empty();
    }

    @Override
    public Student updateStudent(String id, Student studentDetails) {
        var existing = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found."));
        validationService.validateEmailOnUpdate(existing.getEmail(), studentDetails.getEmail());

        existing.setIdUser(studentDetails.getIdUser());
        existing.setName(studentDetails.getName());
        existing.setPhone(studentDetails.getPhone());
        existing.setEmail(studentDetails.getEmail());
        existing.setImage(studentDetails.getImage());
        existing.setSex(studentDetails.getSex());
        existing.setPrograms(Optional.ofNullable(studentDetails.getPrograms()).orElse(List.of()));
        existing.setStudentID(studentDetails.getStudentID());
        existing.setStatus(studentDetails.isStatus());
        existing.setSemester(studentDetails.getSemester());

        if (studentDetails.getPassword() != null && !studentDetails.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(studentDetails.getPassword()));
        }

        return studentRepository.save(existing);
    }

    @Override
    public Actor updateActor(String id, Actor actorDetails) {
        var existing = actorsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actor not found."));
        validationService.validateEmailOnUpdate(existing.getEmail(), actorDetails.getEmail());

        existing.setIdUser(actorDetails.getIdUser());
        existing.setName(actorDetails.getName());
        existing.setPhone(actorDetails.getPhone());
        existing.setEmail(actorDetails.getEmail());
        existing.setImage(actorDetails.getImage());
        existing.setSex(actorDetails.getSex());
        existing.setPrograms(Optional.ofNullable(actorDetails.getPrograms()).orElse(List.of()));
        existing.setPosition(actorDetails.getPosition());

        if (actorDetails.getPassword() != null && !actorDetails.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(actorDetails.getPassword()));
        }

        return actorsRepository.save(existing);
    }

    @Override
    public void deleteStudent(String id) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found."));
        studentRepository.delete(existing);
    }

    @Override
    public void deleteActor(String id) {
        Actor existing = actorsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actor not found."));
        actorsRepository.delete(existing);
    }
}
