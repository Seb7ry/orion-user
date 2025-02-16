package com.unibague.gradework.orionserver.model;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a User in the system.
 * This class serves as a base DTO for different types of users, such as students and actors.
 */
@Data
@RequiredArgsConstructor
public class UserDTO {

    /**
     * Unique identifier of the user.
     */
    private String idUser;

    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Birth date of the user.
     */
    private LocalDate birthDate;

    /**
     * Contact phone number of the user.
     */
    private String phone;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Profile image URL or path of the user.
     */
    private String image;

    /**
     * Gender of the user.
     */
    private String sex;

    /**
     * Role name assigned to the user.
     */
    private String role;

    /**
     * List of programs associated with the user.
     */
    private List<ProgramDTO> programs;

    /**
     * Constructs a {@link UserDTO} object with the specified user details.
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
     * @param programs  A list of programs associated with the user.
     */
    public UserDTO(String idUser, String firstName, String lastName, LocalDate birthDate,
                   String phone, String email, String image, String sex, String role,
                   List<ProgramDTO> programs) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.image = image;
        this.sex = sex;
        this.role = role;
        this.programs = programs;
    }
}
