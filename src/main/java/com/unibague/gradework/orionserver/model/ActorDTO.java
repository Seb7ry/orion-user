package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ActorDTO extends UserDTO {
    private String employeeId;
    private String position;

    public ActorDTO(String idUser, String firstName, String lastName, LocalDate birthDate,
                    String phone, String email, String image, String sex, String role,
                    List<ProgramDTO> programs, String employeeId, String position) {
        super(idUser, firstName, lastName, birthDate, phone, email, image, sex, role, programs);
        this.employeeId = employeeId;
        this.position = position;
    }
}
