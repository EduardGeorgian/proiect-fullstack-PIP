package org.pipproject.pip_project.business;


import jakarta.transaction.Transactional;
import org.pipproject.pip_project.model.*;
import org.pipproject.pip_project.repositories.AccountRepository;
import org.pipproject.pip_project.repositories.FriendsRepository;
import org.pipproject.pip_project.repositories.TransactionRepository;
import org.pipproject.pip_project.repositories.UserRepository;
import org.pipproject.pip_project.validators.TransactionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository, FriendsRepository friendsRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
    }

    @Transactional
    public Transaction addTransfer(String initiatorEmail, double amount, Account sourceAccount, Account destinationAccount) {
        TransactionValidator.validate(TransactionType.TRANSFER, amount, sourceAccount, destinationAccount);

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction(initiatorEmail, TransactionType.TRANSFER, new Date(), amount, sourceAccount, destinationAccount, TransactionStatus.COMPLETED);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction addWithdrawal(String initiatorEmail, double amount, Account sourceAccount) {
        TransactionValidator.validate(TransactionType.WITHDRAWAL, amount, sourceAccount, null);

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        accountRepository.save(sourceAccount);

        Transaction transaction = new Transaction(initiatorEmail, TransactionType.WITHDRAWAL, new Date(), amount, sourceAccount, null, TransactionStatus.COMPLETED);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction addDeposit(String initiatorEmail, double amount, Account destinationAccount) {
        TransactionValidator.validate(TransactionType.DEPOSIT, amount,null, destinationAccount);

        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction(initiatorEmail, TransactionType.DEPOSIT, new Date(), amount, destinationAccount, null, TransactionStatus.COMPLETED);
        return transactionRepository.save(transaction);
    }


    public List<Transaction> getAllTransactions(String initiatorEmail) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(initiatorEmail);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();

        List<Transaction> initiated = transactionRepository.findByInitiatorEmailOrderByDateDesc(initiatorEmail);
        List<Transaction> received = transactionRepository.findByDestinationAccountIdOrderByDateDesc(user.getId());

        List<Transaction> all = new ArrayList<>();
        if (initiated != null) all.addAll(initiated);
        if (received != null) all.addAll(received);

        all.sort(Comparator.comparing(Transaction::getDate).reversed());

        return all;
    }

    @Transactional
    public void deleteCompletedOrFailedTransactionsForUser(String initiatorEmail) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(initiatorEmail);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }
        User user = userOpt.get();

        List<Transaction> initiated = transactionRepository.findByInitiatorEmailOrderByDateDesc(initiatorEmail);

        List<Account> userAccounts = accountRepository.findByUserId(user.getId());
        List<Transaction> received = new ArrayList<>();
        for (Account account : userAccounts) {
            List<Transaction> accountTransactions = transactionRepository.findByDestinationAccountIdOrderByDateDesc(account.getId());
            if (accountTransactions != null) {
                received.addAll(accountTransactions);
            }
        }

        Set<Transaction> allTransactions = new HashSet<>();
        if (initiated != null) allTransactions.addAll(initiated);
        allTransactions.addAll(received);

        for (Transaction tx : allTransactions) {
            if (tx.getStatus() == TransactionStatus.COMPLETED || tx.getStatus() == TransactionStatus.FAILED) {
                transactionRepository.delete(tx);
            }
        }
    }

}
