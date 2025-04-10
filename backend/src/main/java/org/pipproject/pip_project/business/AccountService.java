package org.pipproject.pip_project.business;

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

}
