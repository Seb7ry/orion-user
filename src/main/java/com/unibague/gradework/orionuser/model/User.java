package com.unibague.gradework.orionuser.model;

import com.unibague.gradework.orionuser.enumerator.TypeSex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "users")
public class User {

    @Id
    private String idUser;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String phone;
    private String image;
    private TypeSex sex;
    private String password;

    @DBRef
    @Indexed
    private Role role;

    @Indexed
    private List<String> programs;
}
