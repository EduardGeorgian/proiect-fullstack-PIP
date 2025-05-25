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


    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestParam Long accountId){
        try{
            accountService.deleteAccount(accountId);
            Map<String,String> response = new HashMap<>();
            response.put("accountId",accountId.toString());
            response.put("message","Account deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            Map<String,String> error = new HashMap<>();
            error.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }


}
