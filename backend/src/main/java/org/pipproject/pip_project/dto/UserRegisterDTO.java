package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for user registration.
 * Contains username, email, and password for new user creation.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    /**
     * Username chosen by the user.
     */
    private String username;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Password chosen by the user.
     */
    private String password;
}
