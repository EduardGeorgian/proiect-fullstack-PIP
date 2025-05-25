package org.pipproject.pip_project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.dto.AccountCreateDTO;
import org.pipproject.pip_project.dto.DepositDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.Currency;
import org.pipproject.pip_project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAccount() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setCurrency(Currency.USD);
        dto.setUser(user);

        Account account = new Account();
        account.setId(1L);
        account.setCurrency(Currency.USD);
        account.setUser(user);

        when(accountService.createAccount(any(), any())).thenReturn(account);

        mockMvc.perform(post("/api/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void testGetAllAccounts() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        Account account = new Account();
        account.setId(1L);
        account.setCurrency(Currency.USD);
        account.setUser(user);

        when(accountService.getAccountsByUser("test@example.com")).thenReturn(List.of(account));

        mockMvc.perform(get("/api/account")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testDepositBalance() throws Exception {
        DepositDTO depositDTO = new DepositDTO();
        depositDTO.setAccountId(1L);
        depositDTO.setUserEmail("test@example.com");
        depositDTO.setAmount(50);

        User user = new User();
        user.setEmail("test@example.com");

        Account account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setBalance(150.0);
        account.setCurrency(Currency.USD);

        when(accountService.depositToAccount(1L, "test@example.com", 50)).thenReturn(account);

        mockMvc.perform(post("/api/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150));
    }
}
