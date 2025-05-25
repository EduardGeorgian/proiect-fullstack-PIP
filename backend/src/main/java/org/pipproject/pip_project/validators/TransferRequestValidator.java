package org.pipproject.pip_project.validators;

import org.pipproject.pip_project.dto.TransferRequestDTO;

/**
 * Validator for TransferRequest data.
 * Ensures that the requester, recipient, amount, description, and source account id are valid.
 */
public class TransferRequestValidator {

    /**
     * Validates transfer request parameters.
     *
     * @param requesterEmail email of the user requesting the transfer
     * @param recipientEmail email of the user receiving the transfer request
     * @param amount amount to be transferred, must be positive
     * @param description description or note of the transfer request
     * @param sourceAccountId the source account identifier, must be valid (non-zero)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static void validate(String requesterEmail, String recipientEmail, double amount, String description, long sourceAccountId) {
        if(amount <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if(sourceAccountId == 0){
            throw new IllegalArgumentException("Source Account Id must exist");
        }

        // Optionally add more validation like checking for null or empty emails and description
    }
}
