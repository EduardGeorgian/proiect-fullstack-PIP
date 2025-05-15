package org.pipproject.pip_project.validators;

import org.pipproject.pip_project.dto.TransferRequestDTO;

public class TransferRequestValidator {
    public static void validate(String requesterEmail, String recipientEmail,double amount, String description, long sourceAccountId) {
        if(amount < 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if(sourceAccountId==0){
            throw new IllegalArgumentException("Source Account Id must exist");
        }
    }
}
