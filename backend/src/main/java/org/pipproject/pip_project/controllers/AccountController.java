package org.pipproject.pip_project.controllers;

import org.pipproject.pip_project.business.AccountService;

import org.pipproject.pip_project.dto.AccountCreateDTO;
import org.pipproject.pip_project.dto.AccountSendTransactionDTO;

import org.pipproject.pip_project.dto.DepositDTO;
import org.pipproject.pip_project.model.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for handling account-related operations such as creating accounts,
 * fetching accounts, and depositing funds.
 */
@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
public class AccountController {
    private final AccountService accountService;

    /**
     * Constructs the AccountController with a provided AccountService.
     *
     * @param accountService the service used for account-related operations
     */
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates a new account based on the provided AccountCreateDTO.
     *
     * @param account the account creation data transfer object
     * @return a {@link ResponseEntity} with the created account or an error message
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody AccountCreateDTO account) {
        try {
            Account responseAccount = accountService.createAccount(account.getCurrency(), account.getUser());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseAccount);
        } catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Retrieves all accounts associated with a given user email.
     *
     * @param email the user's email address
     * @return a {@link ResponseEntity} containing a list of accounts or an error message
     */
    @GetMapping("")
    public ResponseEntity<?> getAllAccounts(@RequestParam String email) {
        try {
            List<Account> accounts = accountService.getAccountsByUser(email);
            if (accounts.isEmpty())
                return ResponseEntity.status(HttpStatus.OK).body(accounts);

            return ResponseEntity.status(HttpStatus.OK).body(accounts);
        } catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Retrieves simplified account information for transaction purposes for a specific user.
     *
     * @param userEmail the email address of the user
     * @return a {@link ResponseEntity} containing a list of {@link AccountSendTransactionDTO} or an error message
     */
    @GetMapping("/{userEmail}")
    public ResponseEntity<?> getAccountsForTransactions(@PathVariable String userEmail){
        try {
            List<AccountSendTransactionDTO> response= accountService.getAccountSendTransactionsByUser(userEmail);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Deposits a specified amount into an account.
     *
     * @param depositDTO the deposit data transfer object containing account ID, user email, and amount
     * @return a {@link ResponseEntity} with the updated account or an error message
     */
    @PostMapping("/deposit")
    public ResponseEntity<?> depositBalance(@RequestBody DepositDTO depositDTO){
        try {
            Account account = accountService.depositToAccount(
                    depositDTO.getAccountId(),
                    depositDTO.getUserEmail(),
                    depositDTO.getAmount()
            );

            return ResponseEntity.ok(account);
        } catch (Exception e) {
            Map<String,String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
