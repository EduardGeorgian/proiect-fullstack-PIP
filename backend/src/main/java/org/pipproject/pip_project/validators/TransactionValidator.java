package org.pipproject.pip_project.validators;

import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.TransactionType;

public class TransactionValidator {
    public static void validate(TransactionType type, double amount, Account sourceAccount, Account destinationAccount){
        if(amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than 0");

        if(type == TransactionType.TRANSFER || type == TransactionType.WITHDRAWAL){
            if(sourceAccount == null)
                throw new IllegalArgumentException("Source account cannot be null");

            if(sourceAccount.getBalance() < amount)
                throw new IllegalArgumentException("Insufficient funds in the source account");
        }

        if(type == TransactionType.TRANSFER){
            if(destinationAccount == null)
                throw new IllegalArgumentException("Destination account cannot be null");
        }

        if(type == TransactionType.REQUEST){
            if(sourceAccount == null || destinationAccount == null)
                throw new IllegalArgumentException("Source account and destination account cannot be null");
        }

        if (type == TransactionType.TRANSFER && sourceAccount.getId() == destinationAccount.getId()) {
            throw new IllegalArgumentException("Source and destination accounts must be different for transfers");
        }
    }
}
