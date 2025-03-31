package org.pipproject.pip_project.controllers;


import org.pipproject.pip_project.business.TransactionService;
import org.pipproject.pip_project.dto.TransactionDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Transaction;
import org.pipproject.pip_project.model.TransactionStatus;
import org.pipproject.pip_project.model.TransactionType;
import org.pipproject.pip_project.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/transactions")
@CrossOrigin("*")
public class TransactionController {
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionController(TransactionService transactionService, AccountRepository accountRepository) {
        this.transactionService = transactionService;
        this.accountRepository = accountRepository;
    }

    @PostMapping("")
    public ResponseEntity<?> addTransaction(@RequestBody TransactionDTO transactionDTO) {
        try {
            if (transactionDTO.getType() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction type is required");
            }

            if (transactionDTO.getAmount() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount must be greater than zero");
            }

            Account sourceAccount = transactionDTO.getSourceAccount() != null ?
                    accountRepository.findById(transactionDTO.getSourceAccount().getId()).orElse(null) : null;
            Account destinationAccount = transactionDTO.getDestinationAccount() != null ?
                    accountRepository.findById(transactionDTO.getDestinationAccount().getId()).orElse(null) : null;

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
