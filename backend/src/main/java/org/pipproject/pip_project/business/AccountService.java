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

/**
 * Service pentru gestionarea conturilor bancare ale utilizatorilor.
 * Oferă funcționalități pentru creare cont, obținere conturi și tranzacții,
 * precum și depuneri de bani în conturi.
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * Constructor pentru AccountService, folosit pentru injectarea dependențelor.
     *
     * @param accountRepository repository-ul pentru conturi
     * @param userRepository    repository-ul pentru utilizatori
     */
    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creează un cont nou pentru un utilizator într-o anumită monedă.
     * Dacă utilizatorul nu există, este salvat automat.
     *
     * @param currency moneda contului
     * @param user     utilizatorul asociat contului
     * @return contul creat
     */
    public Account createAccount(Currency currency, User user) {
        User existingUser = userRepository.findByEmail(user.getEmail()).orElseGet(() -> userRepository.save(user));
        Account account = new Account(currency, existingUser);
        return accountRepository.save(account);
    }

    /**
     * Returnează toate conturile asociate unui utilizator identificat prin email.
     *
     * @param email email-ul utilizatorului
     * @return lista de conturi ale utilizatorului
     * @throws RuntimeException dacă utilizatorul nu este găsit
     */
    public List<Account> getAccountsByUser(String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")));
        return accountRepository.findByUser(user);
    }

    /**
     * Returnează conturile unui user pe baza email-ului, folosit pentru listarea in tranzactii
     *
     * @param email email-ul utilizatorului
     * @return lista DTO-urilor ce conțin informații despre tranzacțiile efectuate
     * @throws RuntimeException dacă utilizatorul nu este găsit
     */
    public List<AccountSendTransactionDTO> getAccountSendTransactionsByUser(String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")));
        return accountRepository.findAccountSendTransactionDTOByUser(user);
    }

    /**
     * Returnează un cont pe baza ID-ului.
     *
     * @param id ID-ul contului
     * @return contul găsit
     * @throws RuntimeException dacă contul nu este găsit
     */
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    /**
     * Depune o sumă de bani într-un cont, validând identitatea utilizatorului și suma.
     * Această metodă este tranzacțională.
     *
     * @param accountId ID-ul contului în care se face depunerea
     * @param userEmail email-ul utilizatorului care deține contul
     * @param amount    suma ce va fi depusă
     * @return contul actualizat după depunere
     * @throws Exception dacă contul nu există, utilizatorul nu este autorizat sau suma este invalidă
     */
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

    @Transactional
    public void deleteAccount(Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new RuntimeException("Account not found");
        }
        Account account = accountOpt.get();
        accountRepository.delete(account);
    }
}
