package org.pipproject.pip_project.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.business.TransactionService;
import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.dto.TransactionDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(controllers = org.pipproject.pip_project.controllers.TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AccountService accountService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Account sourceAccount;
    private Account destAccount;

    @BeforeEach
    void setup() {
        sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setBalance(1000.0);

        destAccount = new Account();
        destAccount.setId(2L);
        destAccount.setBalance(500.0);
    }

    @Test
    void addTransfer_success() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        dto.setInitiatorEmail("user@example.com");
        dto.setAmount(100);
        dto.setSourceAccountId(1L);
        dto.setDestinationAccountId(2L);

        Transaction transaction = new Transaction();
        transaction.setAmount(100);
        transaction.setType(org.pipproject.pip_project.model.TransactionType.TRANSFER);

        when(accountService.getAccountById(1L)).thenReturn(sourceAccount);
        when(accountService.getAccountById(2L)).thenReturn(destAccount);
        when(transactionService.addTransfer(anyString(), anyDouble(), any(Account.class), any(Account.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.type").value("TRANSFER"));
    }

    @Test
    void addTransfer_missingAccounts_badRequest() throws Exception {
        TransactionDTO dto = new TransactionDTO();
        dto.setInitiatorEmail("user@example.com");
        dto.setAmount(100);
        // missing sourceAccountId and destinationAccountId

        mockMvc.perform(post("/api/transactions/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTransactions_success() throws Exception {
        List<Transaction> transactions = List.of(new Transaction());

        when(transactionService.getAllTransactions("user@example.com")).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("initiatorEmail", "user@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserTransactions_success() throws Exception {
        doNothing().when(transactionService).deleteCompletedOrFailedTransactionsForUser("user@example.com");

        mockMvc.perform(delete("/api/transactions/clear")
                        .param("initiatorEmail", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("User's completed and failed transactions deleted."));
    }
}
