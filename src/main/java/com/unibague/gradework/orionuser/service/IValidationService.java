package com.unibague.gradework.orionuser.service;

import com.unibague.gradework.orionuser.model.Role;

public interface IValidationService {

    Role validateRole(Role role);

    void validateEmail(String email);

    void validateEmailOnUpdate(String existingEmail, String newEmail);

    void validateIdUser(String idUser);
}
