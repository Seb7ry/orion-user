package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class StudentDTO extends UserDTO {
    private String studentID;
    private boolean status;
    private String semester;
    private String category;

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
