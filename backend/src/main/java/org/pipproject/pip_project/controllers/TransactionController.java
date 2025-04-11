package org.pipproject.pip_project.controllers;


import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.business.TransactionService;
import org.pipproject.pip_project.dto.TransactionDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Transaction;
import org.pipproject.pip_project.model.TransactionStatus;
import org.pipproject.pip_project.model.TransactionType;
import org.pipproject.pip_project.repositories.AccountRepository;
import org.pipproject.pip_project.validators.TransactionDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
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

    @PostMapping("")
    public ResponseEntity<?> addTransaction(@RequestBody TransactionDTO transactionDTO) {
        try {
            TransactionDTOValidator.validate(transactionDTO);

            Account sourceAccount = transactionDTO.getSourceAccountId() != null ?
                    accountService.getAccountById(transactionDTO.getSourceAccountId()) : null;
            Account destinationAccount = transactionDTO.getDestinationAccountId() != null ?
                    accountService.getAccountById(transactionDTO.getDestinationAccountId()) : null;

            if (transactionDTO.getType() == TransactionType.TRANSFER &&
                    (sourceAccount == null || destinationAccount == null)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Both source and destination accounts are required for transfers.");
            }

            if (transactionDTO.getType() == TransactionType.WITHDRAWAL && sourceAccount == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Source account is required for withdrawals.");
            }

            if (transactionDTO.getType() == TransactionType.DEPOSIT && destinationAccount == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Destination account is required for deposits.");
            }

            Transaction responseTransaction = transactionService.addTransaction(
                    transactionDTO.getType(),
                    transactionDTO.getDate(),
                    transactionDTO.getAmount(),
                    sourceAccount,
                    destinationAccount,
                    TransactionStatus.PENDING
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(responseTransaction);

        } catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
