package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.Role;

public interface IValidationService {

    Role validateRole(Role role);

    void validateEmail(String email);

    void validateEmailOnUpdate(String existingEmail, String newEmail);

    void validateIdUser(String idUser);
}
