package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing an Actor in the system.
 * This class extends {@link UserDTO} and includes additional attributes specific to actors.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ActorDTO extends UserDTO {

    /**
     * Unique identifier for the employee.
     */
    private String employeeId;

    /**
     * Position or job title of the actor.
     */
    private String position;

    /**
     * Constructs an {@link ActorDTO} with all user-related attributes, along with
     * specific fields for an actor.
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
     * @param programs  A list of programs associated with the actor.
     * @param employeeId The unique identifier of the employee.
     * @param position  The position or job title of the actor.
     */
    public ActorDTO(String idUser, String firstName, String lastName, LocalDate birthDate,
                    String phone, String email, String image, String sex, String role,
                    List<ProgramDTO> programs, String employeeId, String position) {
        super(idUser, firstName, lastName, birthDate, phone, email, image, sex, role, programs);
        this.employeeId = employeeId;
        this.position = position;
    }
}
