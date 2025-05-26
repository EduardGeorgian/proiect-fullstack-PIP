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

/**
 * REST controller for managing user friendships, including adding,
 * retrieving, and removing friends.
 */
@RestController
@RequestMapping("api/friends")
@CrossOrigin(origins = "*")
public class FriendsController {
    private final FriendsService friendsService;
    private final UserService userService;

    /**
     * Constructs a FriendsController with required services.
     *
     * @param friendsService service to manage friendship logic
     * @param userService    service to handle user-related operations
     */
    @Autowired
    public FriendsController(FriendsService friendsService, UserService userService) {
        this.friendsService = friendsService;
        this.userService = userService;
    }

    /**
     * Sends a friend request from one user to another.
     *
     * @param friendRequest DTO containing user ID and friend ID
     * @return a {@link ResponseEntity} indicating success or failure
     */
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

    /**
     * Retrieves a list of all friends for a given user.
     *
     * @param id the ID of the user whose friends are to be fetched
     * @return a {@link ResponseEntity} with the list of friends or an error
     */
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

    /**
     * Removes a friend from the user's friend list.
     *
     * @param userId   the ID of the user
     * @param friendId the ID of the friend to remove
     * @return a {@link ResponseEntity} indicating success or failure
     */
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

    /**
     * Accepts a friend request sent by another user.
     *
     * @param friendRequest DTO containing the sender and current user IDs
     * @return a {@link ResponseEntity} indicating success or failure
     */
    @PostMapping("requests/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendRequestDTO friendRequest) {
        try {
            User currentUser = userService.findUserById(friendRequest.getUser_id()); // cel care accepta
            User sender = userService.findUserById(friendRequest.getFriend_id()); // cel care a trimis cererea

            friendsService.acceptFriendRequest(currentUser, sender);
            return ResponseEntity.status(HttpStatus.OK).body("Friend request accepted");
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Rejects a friend request sent by another user.
     *
     * @param friendRequest DTO containing the sender and current user IDs
     * @return a {@link ResponseEntity} indicating success or failure
     */
    @PostMapping("requests/reject")
    public ResponseEntity<?> rejectFriendRequest(@RequestBody FriendRequestDTO friendRequest) {
        try {
            User currentUser = userService.findUserById(friendRequest.getUser_id()); // cel care respinge
            User sender = userService.findUserById(friendRequest.getFriend_id()); // cel care a trimis cererea

            friendsService.rejectFriendRequest(currentUser, sender);
            return ResponseEntity.status(HttpStatus.OK).body("Friend request rejected");
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Lists all pending friend requests received by a user.
     *
     * @param userId the ID of the current user
     * @return a {@link ResponseEntity} with the list of pending requests
     */
    @GetMapping("/requests/received")
    public ResponseEntity<?> getReceivedFriendRequests(@RequestParam long userId) {
        try {
            User user = userService.findUserById(userId);
            List<User> pendingSenders = friendsService.getReceivedFriendRequests(user);

            if (pendingSenders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(pendingSenders);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
