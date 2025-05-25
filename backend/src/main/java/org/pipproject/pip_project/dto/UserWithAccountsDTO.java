package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.User;

import java.util.List;

/**
 * Data Transfer Object containing user details along with their associated accounts.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithAccountsDTO {
    /**
     * User information.
     */
    private User user;

    /**
     * List of accounts owned by the user.
     */
    private List<Account> accounts;
}
