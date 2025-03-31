package org.pipproject.pip_project.business;


import jakarta.transaction.Transactional;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Transaction;
import org.pipproject.pip_project.model.TransactionStatus;
import org.pipproject.pip_project.model.TransactionType;
import org.pipproject.pip_project.repositories.AccountRepository;
import org.pipproject.pip_project.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction addTransaction(TransactionType type, Date date, double amount, Account sourceAccount, Account destinationAccount, TransactionStatus status) {
        if(amount<=0)
            throw new IllegalArgumentException("Amount must be greater than 0");

        if(type == TransactionType.TRANSFER || type == TransactionType.WITHDRAWAL){
            if(sourceAccount==null){
                throw new IllegalArgumentException("Source account cannot be null");
            }
            if(sourceAccount.getBalance()<amount){
                throw new IllegalArgumentException("Insufficient balance in the source account");
            }
        }

        if(type == TransactionType.TRANSFER || type == TransactionType.DEPOSIT){
            if (destinationAccount == null) {
                throw new IllegalArgumentException("Destination account is required for this transaction type.");
            }
        }

        Transaction transaction = new Transaction(type, date, amount, sourceAccount, destinationAccount, status);

        if (type == TransactionType.TRANSFER || type == TransactionType.WITHDRAWAL) {
            sourceAccount.setBalance(sourceAccount.getBalance() - amount);
            accountRepository.save(sourceAccount);
        }

        if (type == TransactionType.TRANSFER || type == TransactionType.DEPOSIT) {
            destinationAccount.setBalance(destinationAccount.getBalance() + amount);
            accountRepository.save(destinationAccount);
        }

        return transactionRepository.save(transaction);

    }

}
