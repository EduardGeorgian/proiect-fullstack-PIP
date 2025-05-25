package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pipproject.pip_project.model.Currency;
import org.pipproject.pip_project.model.User;

/**
 * Data Transfer Object used when creating a new account.
 * Contains the currency in which the account will operate
 * and the user to whom the account will belong.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDTO {

    /**
     * The currency type for the account (e.g., EUR, USD, RON).
     */
    private Currency currency;

    /**
     * The {@link User} who owns the account.
     */
    private User user;
}
