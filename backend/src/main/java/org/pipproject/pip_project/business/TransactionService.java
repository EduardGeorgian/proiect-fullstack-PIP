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
    public Transaction addTransfer(String initiatorEmail, double amount, Account sourceAccount, Account destinationAccount) {
        TransactionValidator.validate(TransactionType.TRANSFER, amount, sourceAccount, destinationAccount);

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction(initiatorEmail, TransactionType.TRANSFER, new Date(), amount, sourceAccount, destinationAccount, TransactionStatus.PENDING);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction addWithdrawal(String initiatorEmail, double amount, Account sourceAccount) {
        TransactionValidator.validate(TransactionType.WITHDRAWAL, amount, sourceAccount, null);

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        accountRepository.save(sourceAccount);

        Transaction transaction = new Transaction(initiatorEmail, TransactionType.WITHDRAWAL, new Date(), amount, sourceAccount, null, TransactionStatus.PENDING);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction addDeposit(String initiatorEmail, double amount, Account destinationAccount) {
        TransactionValidator.validate(TransactionType.DEPOSIT, amount, destinationAccount, null);

        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction(initiatorEmail, TransactionType.DEPOSIT, new Date(), amount, destinationAccount, null, TransactionStatus.PENDING);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions(String initiatorEmail) {
        return transactionRepository.findByInitiatorEmailOrderByDateDesc(initiatorEmail);
    }

}
