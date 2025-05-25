package org.pipproject.pip_project.validators;

import org.pipproject.pip_project.dto.TransactionDTO;

/**
 * Validator for TransactionDTO objects.
 * Ensures required fields are valid before processing.
 */
public class TransactionDTOValidator {

    /**
     * Validates the TransactionDTO.
     * Checks that the transaction type is set and amount is positive.
     *
     * @param transactionDTO the transaction DTO to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validate(TransactionDTO transactionDTO) {
        if(transactionDTO.getType() == null)
            throw new IllegalArgumentException("TransactionDTO must have a type");

        if(transactionDTO.getAmount() <= 0)
            throw new IllegalArgumentException("Amount must be greater than 0");
    }
}
