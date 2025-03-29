package org.pipproject.pip_project.controllers;

import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.dto.AccountCreateDTO;
import org.pipproject.pip_project.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user")
    public ResponseEntity<?> getAllAccounts(@RequestParam String email){
        List<Account> accounts = accountService.getAccountsByUser(email);
        return ResponseEntity.status(HttpStatus.OK).body(accounts);
    }
}
