package org.pipproject.pip_project.controllers;


import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.business.TransactionService;
import org.pipproject.pip_project.dto.TransactionDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Transaction;
import org.pipproject.pip_project.model.TransactionStatus;
import org.pipproject.pip_project.model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    private final TransactionService transactionService;
    private final AccountService accountService;

    @Autowired
    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> addTransfer(@RequestBody TransactionDTO transactionDTO) {
        try {
            if (transactionDTO.getSourceAccountId() == null || transactionDTO.getDestinationAccountId() == null) {
                return ResponseEntity.badRequest().body("Source and destination accounts are required.");
            }

            Account sourceAccount = accountService.getAccountById(transactionDTO.getSourceAccountId());
            Account destinationAccount = accountService.getAccountById(transactionDTO.getDestinationAccountId());

            Transaction transaction = transactionService.addTransfer(
                    transactionDTO.getInitiatorEmail(),
                    transactionDTO.getAmount(),
                    sourceAccount,
                    destinationAccount
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> addWithdrawal(@RequestBody TransactionDTO transactionDTO) {
        try {
            if (transactionDTO.getSourceAccountId() == null) {
                return ResponseEntity.badRequest().body("Source account is required.");
            }

            Account sourceAccount = accountService.getAccountById(transactionDTO.getSourceAccountId());

            Transaction transaction = transactionService.addWithdrawal(
                    transactionDTO.getInitiatorEmail(),
                    transactionDTO.getAmount(),
                    sourceAccount
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> addDeposit(@RequestBody TransactionDTO transactionDTO) {
        try {
            if (transactionDTO.getDestinationAccountId() == null) {
                return ResponseEntity.badRequest().body("Destination account is required.");
            }

            Account destinationAccount = accountService.getAccountById(transactionDTO.getDestinationAccountId());

            Transaction transaction = transactionService.addDeposit(
                    transactionDTO.getInitiatorEmail(),
                    transactionDTO.getAmount(),
                    destinationAccount
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllTransactions(@RequestParam String initiatorEmail) {
        try{
            List<Transaction> transactions = transactionService.getAllTransactions(initiatorEmail);
            if (transactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transactions found.");
            }
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        }catch (Exception e){
            Map<String,String> response = new HashMap<>();
            response.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> deleteUserTransactions(@RequestParam String initiatorEmail) {
        try {
            transactionService.deleteCompletedOrFailedTransactionsForUser(initiatorEmail);
            return ResponseEntity.ok("User's completed and failed transactions deleted.");
        } catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
