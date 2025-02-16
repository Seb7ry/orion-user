package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a Student in the system.
 * This class extends {@link UserDTO} and includes additional attributes specific to students.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class StudentDTO extends UserDTO {

    /**
     * Unique identifier for the student.
     */
    private String studentID;

    /**
     * Indicates whether the student is currently active.
     */
    private boolean status;

    /**
     * The current semester the student is enrolled in.
     */
    private String semester;

    /**
     * The category of the student (e.g., full-time, part-time, scholarship).
     */
    private String category;

    /**
     * Constructs a {@link StudentDTO} with all user-related attributes, along with
     * specific fields for a student.
     *
     * @param idUser    The unique identifier of the user.
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @param birthDate The birth date of the user.
     * @param phone     The contact phone number of the user.
     * @param email     The email address of the user.
     * @param image     The profile image URL or path of the user.
     * @param sex       The gender of the user.
     * @param role      The role name assigned to the user.
     * @param programs  A list of programs associated with the student.
     * @param studentID The unique identifier of the student.
     * @param status    The student's active status.
     * @param semester  The student's current semester.
     * @param category  The category of the student (e.g., full-time, part-time).
     */
    public StudentDTO(String idUser, String firstName, String lastName, LocalDate birthDate,
                      String phone, String email, String image, String sex, String role,
                      List<ProgramDTO> programs, String studentID, boolean status,
                      String semester, String category) {
        super(idUser, firstName, lastName, birthDate, phone, email, image, sex, role, programs);
        this.studentID = studentID;
        this.status = status;
        this.semester = semester;
        this.category = category;
    }
}
