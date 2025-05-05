package org.pipproject.pip_project.validators;

import org.pipproject.pip_project.dto.TransactionDTO;

public class TransactionDTOValidator {
    public static void validate(TransactionDTO transactionDTO) {
        if(transactionDTO.getType() == null)
            throw new IllegalArgumentException(("TransactionDTO must have a type"));


        if(transactionDTO.getAmount() <=0)
            throw new IllegalArgumentException(("Amount must be greater than 0"));

    }
}
