package org.pipproject.pip_project.dto;

import lombok.Data;

/**
 * Data Transfer Object for user login credentials.
 * Contains the email and password provided during login.
 */
@Data
public class LoginDTO {

    /**
     * The email address of the user trying to log in.
     */
    private String email;

    /**
     * The password of the user.
     */
    private String password;
}
