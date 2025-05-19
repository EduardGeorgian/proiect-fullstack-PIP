package org.pipproject.pip_project.controllers;

import org.pipproject.pip_project.business.FriendsService;
import org.pipproject.pip_project.business.UserService;
import org.pipproject.pip_project.dto.FriendRequestDTO;
import org.pipproject.pip_project.model.Friends;
import org.pipproject.pip_project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/friends")
@CrossOrigin(origins = "*")
public class FriendsController {
    private final FriendsService friendsService;
    private final UserService userService;

    @Autowired
    public FriendsController(FriendsService friendsService, UserService userService) {
        this.friendsService = friendsService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFriend(@RequestBody FriendRequestDTO friendRequest) {
        try{
            User user = userService.findUserById(friendRequest.getUser_id());
            User friend = userService.findUserById(friendRequest.getFriend_id());
            friendsService.sendFriendRequest(user,friend);
            return ResponseEntity.status(HttpStatus.CREATED).body("Friend request added");
        }catch (Exception e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllFriends(@RequestParam(required = false) long id) {
        try{
            User user = userService.findUserById(id);
            List<User> response = friendsService.getAllFriends(user);
            if(response.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/unfriend")
    public ResponseEntity<?> unfriend(@RequestParam long userId, @RequestParam long friendId) {
        try{
            User user = userService.findUserById(userId);
            User friend = userService.findUserById(friendId);

            friendsService.deleteFriend(user,friend);
            return new ResponseEntity<>("Friend deleted successfully",HttpStatus.OK);
        }catch (Exception e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
