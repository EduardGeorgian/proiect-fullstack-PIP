package org.pipproject.pip_project.controllers;

import jakarta.websocket.server.PathParam;
import org.pipproject.pip_project.business.UserService;
import org.pipproject.pip_project.dto.LoginDTO;
import org.pipproject.pip_project.dto.UserRegisterDTO;
import org.pipproject.pip_project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins="*")//pentru accesare din frontend, de pe alt domeniu decat local
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRegisterDTO user) {
        try {
            User responseUser = userService.addUser(user.getUsername(), user.getEmail(), user.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")//path parameter, dynamic
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            User requestedUser = userService.findUserById(userId);
            return ResponseEntity.ok(requestedUser);
        }
        catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

//    @GetMapping()
//    public ResponseEntity<?> getUser2(@PathParam("userId") Long userId) {
//        try {
//            User requestedUser = userService.findUserById(userId);
//            return ResponseEntity.ok(requestedUser);
//        }
//        catch (Exception e) {
//            Map<String,String> response = new HashMap<>();
//            response.put("error",e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try{
            if(userService.validateUserCredentials(loginDTO.getEmail(),loginDTO.getPassword()))
                return ResponseEntity.ok("success");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid username or password");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid username or password");
        }
    }

}
