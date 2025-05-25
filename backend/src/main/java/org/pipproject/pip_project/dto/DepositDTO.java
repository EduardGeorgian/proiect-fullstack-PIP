package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for performing a deposit operation into a user's account.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositDTO {

    /**
     * The unique identifier of the account where the deposit will be made.
     */
    private long accountId;

    /**
     * The email address of the user making the deposit.
     */
    private String userEmail;

    /**
     * The amount to be deposited into the account.
     */
    private double amount;
}
