package org.pipproject.pip_project.controllers;

import org.pipproject.pip_project.business.AccountService;
import org.pipproject.pip_project.business.UserService;
import org.pipproject.pip_project.dto.LoginDTO;
import org.pipproject.pip_project.dto.UserRegisterDTO;
import org.pipproject.pip_project.dto.UserWithAccountsDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller that handles user-related operations such as registration, login,
 * and retrieving user details.
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AccountService accountService;
    private final UserRepository userRepository;

    /**
     * Constructor for {@link UserController}.
     *
     * @param userService    service handling user logic
     * @param accountService service handling account-related logic
     */
    @Autowired
    public UserController(UserService userService, AccountService accountService, UserRepository userRepository) {
        this.userService = userService;
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user.
     *
     * @param user data for registration
     * @return the created {@link User} or error message
     */
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

    /**
     * Retrieves a user and their associated accounts by user ID.
     *
     * @param userId ID of the user
     * @return {@link UserWithAccountsDTO} object or error message
     */
    @GetMapping("/{userId}")
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

    /**
     * Validates user login credentials.
     *
     * @param loginDTO contains email and password
     * @return {@link User} if login is successful, or error message if not
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            if (userService.validateUserCredentials(loginDTO.getEmail(), loginDTO.getPassword())) {
                User requestedUser = userService.findUserByEmail(loginDTO.getEmail());
                return ResponseEntity.status(HttpStatus.OK).body(requestedUser);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid password");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("users")
    public ResponseEntity<?> getAllUsers(@RequestParam String userEmail){
        try{
            List<User> filteredUsers = userService.findAllUsers(userEmail);
            return ResponseEntity.status(HttpStatus.OK).body(filteredUsers);
        }catch (Exception e){
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
