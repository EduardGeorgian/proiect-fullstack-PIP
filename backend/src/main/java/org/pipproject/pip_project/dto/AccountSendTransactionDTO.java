package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pipproject.pip_project.model.Currency;

/**
 * Data Transfer Object used for exposing limited account information
 * when a user accesses another user's accounts for the purpose of sending transactions.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSendTransactionDTO {

    /**
     * The unique identifier of the account.
     */
    private Long id;

    /**
     * The currency associated with the account (e.g., RON, USD, EUR).
     */
    private Currency currency;
}
