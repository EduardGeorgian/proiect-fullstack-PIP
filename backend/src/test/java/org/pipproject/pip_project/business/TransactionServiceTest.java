package org.pipproject.pip_project.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.model.*;
import org.pipproject.pip_project.repositories.*;

import java.util.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendsRepository friendsRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTransfer_success() {
        Account source = new Account();
        source.setId(1L);
        source.setBalance(200.0);

        Account dest = new Account();
        dest.setId(2L);
        dest.setBalance(100.0);

        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Transaction tx = transactionService.addTransfer("initiator@example.com", 50.0, source, dest);

        assertEquals(TransactionType.TRANSFER, tx.getType());
        assertEquals(150.0, source.getBalance());
        assertEquals(150.0, dest.getBalance());
        verify(transactionRepository, times(1)).save(tx);
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void testAddWithdrawal_success() {
        Account source = new Account();
        source.setBalance(300.0);

        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Transaction tx = transactionService.addWithdrawal("user@example.com", 100.0, source);

        assertEquals(TransactionType.WITHDRAWAL, tx.getType());
        assertEquals(200.0, source.getBalance());
        verify(transactionRepository).save(tx);
        verify(accountRepository).save(source);
    }

    @Test
    void testAddDeposit_success() {
        Account dest = new Account();
        dest.setBalance(100.0);

        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Transaction tx = transactionService.addDeposit("user@example.com", 200.0, dest);

        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(300.0, dest.getBalance());
        verify(transactionRepository).save(tx);
        verify(accountRepository).save(dest);
    }

    @Test
    void testGetAllTransactions_userNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.getAllTransactions("unknown@example.com");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllTransactions_success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        Transaction tx1 = new Transaction();
        tx1.setDate(new Date());
        Transaction tx2 = new Transaction();
        tx2.setDate(new Date());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findByInitiatorEmailOrderByDateDesc("user@example.com")).thenReturn(List.of(tx1));
        when(transactionRepository.findByDestinationAccountIdOrderByDateDesc(user.getId())).thenReturn(List.of(tx2));

        List<Transaction> allTx = transactionService.getAllTransactions("user@example.com");
        assertTrue(allTx.contains(tx1));
        assertTrue(allTx.contains(tx2));
    }
}
