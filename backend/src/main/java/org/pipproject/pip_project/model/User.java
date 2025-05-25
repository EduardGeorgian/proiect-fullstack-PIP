package org.pipproject.pip_project.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing an application user.
 * Stores user credentials and identification details.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
public class User {

    /**
     * Unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username chosen by the user.
     */
    private String username;

    /**
     * Email address of the user.
     */
    @Column
    private String email;

    /**
     * Hashed password of the user.
     */
    private String password;

    /**
     * Constructor for user with basic fields.
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
