package org.pipproject.pip_project.controllers;


import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.business.UserService;
import org.pipproject.pip_project.dto.LoginDTO;
import org.pipproject.pip_project.dto.UserRegisterDTO;
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

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")//pentru accesare din frontend, de pe alt domeniu decat local
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRegisterDTO user) {
        try {
            User responseUser = userService.addUser(user.getUsername(), user.getEmail(), user.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{userId}")//path parameter, dynamic
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            User requestedUser = userService.findUserById(userId);
            List<Account> accountList = accountService.getAccountsByUser(requestedUser.getEmail());
            UserWithAccountsDTO response = new UserWithAccountsDTO(requestedUser, accountList);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            if (userService.validateUserCredentials(loginDTO.getEmail(), loginDTO.getPassword())) {
                User requestedUser = userService.findUserByEmail(loginDTO.getEmail());
                return ResponseEntity.status(HttpStatus.OK).body(requestedUser);
            }
            else{
                Map<String, String> response = new HashMap<>();
                response.put("error","Invalid password");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
