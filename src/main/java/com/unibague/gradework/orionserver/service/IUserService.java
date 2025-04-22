package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.*;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    Student createStudent(Student student);

    Actor createActor(Actor actor);

    List<StudentDTO> getAllStudentsDTO();

    List<ActorDTO> getAllActorsDTO();

    Optional<StudentDTO> getStudentDTOById(String id);

    Optional<ActorDTO> getActorDTOById(String id);

    Optional<Student> getStudentById(String id);

    Optional<Actor> getActorById(String id);

    Optional<UserLogDTO> findUserByEmail(String email);

    Student updateStudent(String id, Student studentDetails);

    Actor updateActor(String id, Actor actorDetails);

    void deleteStudent(String id);

    void deleteActor(String id);
}
