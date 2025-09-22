package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.exception.UserExceptions;
import com.unibague.gradework.orionuser.model.*;
import com.unibague.gradework.orionuser.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements IUserService {

    private final IProgramService programService;
    private final StudentRepository studentRepository;
    private final ActorRepository actorRepository;
    private final PasswordEncoder passwordEncoder;
    private final IValidationService validationService;

    private static final int AUTO_PWD_BYTES = 24; // ~32 chars base64-url

    public UserService(IProgramService programService,
                       StudentRepository studentRepository,
                       ActorRepository actorRepository,
                       PasswordEncoder passwordEncoder,
                       IValidationService validationService) {
        this.programService = programService;
        this.studentRepository = studentRepository;
        this.actorRepository = actorRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    // ============ HELPERS ============

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    /** Genera una contraseña aleatoria segura (para cumplir NOT NULL) */
    private static String generateRandomPassword() {
        byte[] bytes = new byte[AUTO_PWD_BYTES];
        new SecureRandom().nextBytes(bytes);
        // Base64 URL-safe sin padding, corta y robusta
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String passwordToPersist(String rawPasswordOrNull) {
        String raw = rawPasswordOrNull;
        if (isBlank(raw)) {
            raw = generateRandomPassword();
            log.debug("Generando password interno para cuenta federada (SSO)");
        }
        return passwordEncoder.encode(raw);
    }

    // ============ CREATE ============

    @Override
    public Student createStudent(Student student) {
        log.info("Creating student - ID: {}, Email: {}, Programs: {}",
                student.getIdUser(), student.getEmail(),
                student.getPrograms() != null ? student.getPrograms().size() : 0);

        validationService.validateIdUser(student.getIdUser());
        validationService.validateEmail(student.getEmail());
        validationService.validateStudentId(student.getStudentID());

        // Contraseña:
        // - Si viene por credenciales locales => validar contraseña.
        // - Si viene por SSO (Google) y viene vacía => NO validar y generar interna.
        if (!isBlank(student.getPassword())) {
            validationService.validatePassword(student.getPassword());
        } else {
            log.info("Creación de estudiante sin contraseña detectada (flujo federado/SSO).");
        }

        // Estado por defecto: si no usas wrapper Boolean, al menos no rechazar 'false' por defecto.
        // El check anterior tiraba error si venía en false:
        // if (!student.isStatus()) { throw ... }
        // Lo eliminamos para permitir que el status venga establecido por quien llama.
        // Si quieres forzar true por defecto:
        // student.setStatus(student.isStatus() || true);

        student.setRole(validationService.validateRole(student.getRole()));
        student.setPassword(passwordToPersist(student.getPassword()));

        if (student.getPrograms() != null && !student.getPrograms().isEmpty()) {
            student.setPrograms(student.getPrograms());
        } else {
            student.setPrograms(List.of());
        }

        Student saved = studentRepository.save(student);
        log.info("Student created successfully with ID: {}", saved.getIdUser());
        return saved;
    }

    @Override
    public Actor createActor(Actor actor) {
        log.info("Creating actor - ID: {}, Email: {}, Position: {}, Programs: {}",
                actor.getIdUser(), actor.getEmail(), actor.getPosition(),
                actor.getPrograms() != null ? actor.getPrograms().size() : 0);

        validationService.validateIdUser(actor.getIdUser());
        validationService.validateEmail(actor.getEmail());

        // Contraseña local vs federada
        if (!isBlank(actor.getPassword())) {
            validationService.validatePassword(actor.getPassword());
        } else {
            log.info("Creación de actor sin contraseña detectada (flujo federado/SSO).");
        }

        actor.setRole(validationService.validateRole(actor.getRole()));
        actor.setPassword(passwordToPersist(actor.getPassword()));

        if (actor.getPrograms() != null && !actor.getPrograms().isEmpty()) {
            actor.setPrograms(actor.getPrograms());
        } else {
            actor.setPrograms(List.of());
        }

        Actor saved = actorRepository.save(actor);
        log.info("Actor created successfully with ID: {}", saved.getIdUser());
        return saved;
    }

    // ============ READ LISTS ============

    @Override
    public List<StudentDTO> getAllStudentsDTO() {
        log.debug("Retrieving all students as DTOs");
        List<Student> students = studentRepository.findAll();

        return students.stream().map(student -> {
            List<ProgramDTO> programs = programService.getProgramById(
                    Optional.ofNullable(student.getPrograms()).orElse(List.of())
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
        log.debug("Retrieving all actors as DTOs");
        List<Actor> actors = actorRepository.findAll();

        return actors.stream().map(actor -> {
            List<ProgramDTO> programs = programService.getProgramById(
                    Optional.ofNullable(actor.getPrograms()).orElse(List.of())
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

    // ============ READ SINGLE DTO ============

    @Override
    public Optional<StudentDTO> getStudentDTOById(String id) {
        log.debug("Retrieving student DTO by ID: {}", id);
        return studentRepository.findById(id).map(student -> {
            List<ProgramDTO> programs = programService.getProgramById(
                    Optional.ofNullable(student.getPrograms()).orElse(List.of())
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
        log.debug("Retrieving actor DTO by ID: {}", id);
        return actorRepository.findById(id).map(actor -> {
            List<ProgramDTO> programs = programService.getProgramById(
                    Optional.ofNullable(actor.getPrograms()).orElse(List.of())
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

    // ============ READ ENTITIES ============

    @Override
    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Actor> getActorById(String id) {
        return actorRepository.findById(id);
    }

    @Override
    public Optional<UserLogDTO> findUserByEmail(String email) {
        log.debug("Finding user by email: {}", email);

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

        Optional<Actor> actorOpt = actorRepository.findByEmail(email);
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

    // ============ UPDATE ============

    @Override
    public Student updateStudent(String id, Student studentDetails) {
        log.info("Updating student with ID: {}", id);

        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new UserExceptions.StudentNotFoundException(id));

        validationService.validateEmailOnUpdate(existing.getEmail(), studentDetails.getEmail());
        if (studentDetails.getRole() != null) {
            existing.setRole(validationService.validateRole(studentDetails.getRole()));
        }

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

        if (!isBlank(studentDetails.getPassword())) {
            existing.setPassword(passwordEncoder.encode(studentDetails.getPassword()));
        }

        Student saved = studentRepository.save(existing);
        log.info("Student updated successfully: {}", id);
        return saved;
    }

    @Override
    public Actor updateActor(String id, Actor actorDetails) {
        log.info("Updating actor with ID: {}", id);

        Actor existing = actorRepository.findById(id)
                .orElseThrow(() -> new UserExceptions.ActorNotFoundException(id));

        validationService.validateEmailOnUpdate(existing.getEmail(), actorDetails.getEmail());
        if (actorDetails.getRole() != null) {
            existing.setRole(validationService.validateRole(actorDetails.getRole()));
        }

        existing.setIdUser(actorDetails.getIdUser());
        existing.setName(actorDetails.getName());
        existing.setPhone(actorDetails.getPhone());
        existing.setEmail(actorDetails.getEmail());
        existing.setImage(actorDetails.getImage());
        existing.setSex(actorDetails.getSex());
        existing.setPrograms(Optional.ofNullable(actorDetails.getPrograms()).orElse(List.of()));
        existing.setPosition(actorDetails.getPosition());

        if (!isBlank(actorDetails.getPassword())) {
            existing.setPassword(passwordEncoder.encode(actorDetails.getPassword()));
        }

        Actor saved = actorRepository.save(existing);
        log.info("Actor updated successfully: {}", id);
        return saved;
    }

    // ============ DELETE ============

    @Override
    public void deleteStudent(String id) {
        log.info("Deleting student with ID: {}", id);
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new UserExceptions.StudentNotFoundException(id));
        studentRepository.delete(existing);
    }

    @Override
    public void deleteActor(String id) {
        log.info("Deleting actor with ID: {}", id);
        Actor existing = actorRepository.findById(id)
                .orElseThrow(() -> new UserExceptions.ActorNotFoundException(id));
        actorRepository.delete(existing);
    }
}