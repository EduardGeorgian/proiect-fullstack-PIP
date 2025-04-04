package org.pipproject.pip_project.controllers;

import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.business.UserService;
import org.pipproject.pip_project.dto.AccountCreateDTO;
import org.pipproject.pip_project.dto.AccountSendTransactionDTO;
import org.pipproject.pip_project.dto.UserWithAccountsDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody AccountCreateDTO account){
        try{
           Account responseAccount = accountService.createAccount(account.getCurrency(),account.getUser());
           return ResponseEntity.status(HttpStatus.CREATED).body(responseAccount);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllAccounts(@RequestParam String email){
        try {
            List<Account> accounts = accountService.getAccountsByUser(email);
            if (accounts.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No accounts found");

            return ResponseEntity.status(HttpStatus.OK).body(accounts);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

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
}
