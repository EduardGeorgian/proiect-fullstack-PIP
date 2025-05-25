package org.pipproject.pip_project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.business.UserService;
import org.pipproject.pip_project.dto.LoginDTO;
import org.pipproject.pip_project.dto.UserRegisterDTO;
import org.pipproject.pip_project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_success() throws Exception {
        UserRegisterDTO dto = new UserRegisterDTO("testuser", "test@example.com", "pass");

        User user = new User("testuser", "test@example.com", "encodedpass");
        user.setId(1L);

        when(userService.addUser(anyString(), anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void createUser_existingUser_badRequest() throws Exception {
        UserRegisterDTO dto = new UserRegisterDTO("testuser", "exists@example.com", "pass");

        when(userService.addUser(anyString(), anyString(), anyString()))
                .thenThrow(new Exception("user already exists"));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("user already exists"));
    }

    @Test
    void login_success() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("pass");

        User user = new User("testuser", "test@example.com", "encodedpass");
        user.setId(1L);

        when(userService.validateUserCredentials(anyString(), anyString())).thenReturn(true);
        when(userService.findUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_wrongPassword_badRequest() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("wrongpass");

        when(userService.validateUserCredentials(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid password"));
    }

}
