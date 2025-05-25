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

/**
 * Service pentru gestionarea tranzacțiilor financiare între conturi.
 * Include operațiuni de transfer, retragere și depunere,
 * precum și interogarea și ștergerea tranzacțiilor pentru un utilizator.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    /**
     * Constructor pentru TransactionService.
     *
     * @param transactionRepository repository pentru entitatea Transaction
     * @param accountRepository repository pentru entitatea Account
     * @param userRepository repository pentru entitatea User
     * @param friendsRepository repository pentru entitatea Friends (folosit, deși nu în metodele actuale)
     */
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository, FriendsRepository friendsRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
    }

    /**
     * Adaugă o tranzacție de tip transfer între două conturi.
     * Validează tranzacția, actualizează balanțele și salvează tranzacția.
     *
     * @param initiatorEmail emailul utilizatorului care inițiază transferul
     * @param amount suma transferată
     * @param sourceAccount contul sursă de unde se scade suma
     * @param destinationAccount contul destinatar unde se adaugă suma
     * @return tranzacția creată și salvată în baza de date
     */
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

    /**
     * Adaugă o tranzacție de tip retragere de la un cont.
     * Validează tranzacția, actualizează balanța contului și salvează tranzacția.
     *
     * @param initiatorEmail emailul utilizatorului care inițiază retragerea
     * @param amount suma retrasă
     * @param sourceAccount contul de unde se retrage suma
     * @return tranzacția creată și salvată în baza de date
     */
    @Transactional
    public Transaction addWithdrawal(String initiatorEmail, double amount, Account sourceAccount) {
        TransactionValidator.validate(TransactionType.WITHDRAWAL, amount, sourceAccount, null);

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        accountRepository.save(sourceAccount);

        Transaction transaction = new Transaction(initiatorEmail, TransactionType.WITHDRAWAL, new Date(), amount, sourceAccount, null, TransactionStatus.COMPLETED);
        return transactionRepository.save(transaction);
    }

    /**
     * Adaugă o tranzacție de tip depunere într-un cont.
     * Validează tranzacția, actualizează balanța contului și salvează tranzacția.
     *
     * @param initiatorEmail emailul utilizatorului care inițiază depunerea
     * @param amount suma depusă
     * @param destinationAccount contul în care se depune suma
     * @return tranzacția creată și salvată în baza de date
     */
    @Transactional
    public Transaction addDeposit(String initiatorEmail, double amount, Account destinationAccount) {
        TransactionValidator.validate(TransactionType.DEPOSIT, amount, null, destinationAccount);

        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction(initiatorEmail, TransactionType.DEPOSIT, new Date(), amount, destinationAccount, null, TransactionStatus.COMPLETED);
        return transactionRepository.save(transaction);
    }

    /**
     * Obține toate tranzacțiile legate de un utilizator, atât cele inițiate cât și cele primite.
     * Aruncă excepție dacă utilizatorul nu este găsit.
     *
     * @param initiatorEmail emailul utilizatorului pentru care se interoghează tranzacțiile
     * @return listă de tranzacții sortate descrescător după dată
     * @throws Exception dacă utilizatorul nu este găsit
     */
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

    /**
     * Șterge toate tranzacțiile cu statusul COMPLETED sau FAILED pentru un utilizator.
     * Aruncă excepție dacă utilizatorul nu este găsit.
     *
     * @param initiatorEmail emailul utilizatorului al cărui tranzacții vor fi șterse
     * @throws Exception dacă utilizatorul nu este găsit
     */
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
