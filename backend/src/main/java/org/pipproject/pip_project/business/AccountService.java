package org.pipproject.pip_project.business;

import jakarta.transaction.Transactional;
import org.pipproject.pip_project.dto.AccountSendTransactionDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Currency;
import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.AccountRepository;
import org.pipproject.pip_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createAccount(Currency currency, User user){
        User existingUser = userRepository.findByEmail(user.getEmail()).orElseGet(() -> userRepository.save(user));
        Account account = new Account(currency, existingUser);
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUser(String email){
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")));
        return accountRepository.findByUser(user);
    }

    public List<AccountSendTransactionDTO> getAccountSendTransactionsByUser(String email){
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")));
        return accountRepository.findAccountSendTransactionDTOByUser(user);
    }

    public Account getAccountById(Long id){
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Transactional
    public Account depositToAccount(Long accountId, String userEmail, double amount) throws Exception {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new Exception("Account not found");
        }

        Account account = accountOpt.get();

        if (!account.getUser().getEmail().equals(userEmail)) {
            throw new Exception("Permission denied");
        }

        if (amount <= 0) {
            throw new Exception("Amount must be greater than 0");
        }

        account.setBalance(account.getBalance() + amount);
        return accountRepository.save(account);
    }


}
