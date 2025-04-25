package org.pipproject.pip_project.business;


import jakarta.transaction.Transactional;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Transaction;
import org.pipproject.pip_project.model.TransactionStatus;
import org.pipproject.pip_project.model.TransactionType;
import org.pipproject.pip_project.repositories.AccountRepository;
import org.pipproject.pip_project.repositories.TransactionRepository;
import org.pipproject.pip_project.validators.TransactionValidator;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction addTransaction(String initiatiorEmail,TransactionType type, double amount, Account sourceAccount, Account destinationAccount, TransactionStatus status) {

        TransactionValidator.validate(type, amount, sourceAccount, destinationAccount);

        Transaction transaction = new Transaction(initiatiorEmail,type, new Date(), amount, sourceAccount, destinationAccount, status);

        if (type == TransactionType.TRANSFER) {
            sourceAccount.setBalance(sourceAccount.getBalance() - amount);
            destinationAccount.setBalance(destinationAccount.getBalance() + amount);
            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);
        } else if (type == TransactionType.WITHDRAWAL) {
            sourceAccount.setBalance(sourceAccount.getBalance() - amount);
            accountRepository.save(sourceAccount);
        } else if (type == TransactionType.DEPOSIT) {
            sourceAccount.setBalance(sourceAccount.getBalance() + amount);
            accountRepository.save(sourceAccount);
        }

        return transactionRepository.save(transaction);

    }


    public List<Transaction> getAllTransactions(String initiatorEmail) {
        return transactionRepository.findByInitiatorEmail(initiatorEmail);
    }

}
