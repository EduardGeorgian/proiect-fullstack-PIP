package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pipproject.pip_project.model.TransactionType;

/**
 * Data Transfer Object for transaction details.
 * Contains information about the initiator, type, amount, and source/destination accounts.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    /**
     * Email of the user initiating the transaction.
     */
    private String initiatorEmail;

    /**
     * Type of the transaction (e.g., SEND, WITHDRAW, DEPOSIT).
     */
    private TransactionType type;

    /**
     * The amount involved in the transaction.
     */
    private double amount;

    /**
     * ID of the source account (nullable for deposits).
     */
    private Long sourceAccountId;

    /**
     * ID of the destination account (nullable for withdrawals).
     */
    private Long destinationAccountId;
}
