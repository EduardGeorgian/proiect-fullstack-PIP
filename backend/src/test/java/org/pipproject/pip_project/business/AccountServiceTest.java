package org.pipproject.pip_project.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Currency;
import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.AccountRepository;
import org.pipproject.pip_project.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("TestUser");

        account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setCurrency(Currency.USD);
        account.setBalance(100.0);
    }

    @Test
    void testCreateAccount_NewUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account created = accountService.createAccount(Currency.USD, user);

        assertNotNull(created);
        assertEquals(Currency.USD, created.getCurrency());
        assertEquals(user, created.getUser());

        verify(userRepository).save(user);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testCreateAccount_ExistingUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account created = accountService.createAccount(Currency.USD, user);

        assertNotNull(created);
        assertEquals(user, created.getUser());

        verify(userRepository, never()).save(any());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testGetAccountsByUser_UserExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(accountRepository.findByUser(Optional.of(user))).thenReturn(List.of(account));

        List<Account> accounts = accountService.getAccountsByUser(user.getEmail());

        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals(account, accounts.get(0));
    }

    @Test
    void testGetAccountsByUser_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> accountService.getAccountsByUser(user.getEmail()));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testDepositToAccount_Success() throws Exception {
        account.setBalance(100.0);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Account updatedAccount = accountService.depositToAccount(account.getId(), user.getEmail(), 50.0);

        assertEquals(150.0, updatedAccount.getBalance());
    }

    @Test
    void testDepositToAccount_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class,
                () -> accountService.depositToAccount(1L, user.getEmail(), 50.0));

        assertEquals("Account not found", ex.getMessage());
    }

    @Test
    void testDepositToAccount_PermissionDenied() {
        account.setUser(new User());
        account.getUser().setEmail("other@example.com");

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        Exception ex = assertThrows(Exception.class,
                () -> accountService.depositToAccount(account.getId(), user.getEmail(), 50.0));

        assertEquals("Permission denied", ex.getMessage());
    }

    @Test
    void testDepositToAccount_InvalidAmount() {
        account.setBalance(100.0);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        Exception ex = assertThrows(Exception.class,
                () -> accountService.depositToAccount(account.getId(), user.getEmail(), 0));

        assertEquals("Amount must be greater than 0", ex.getMessage());
    }
}
