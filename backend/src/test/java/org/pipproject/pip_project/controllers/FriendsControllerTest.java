package org.pipproject.pip_project.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.business.FriendsService;
import org.pipproject.pip_project.business.UserService;
import org.pipproject.pip_project.dto.FriendRequestDTO;
import org.pipproject.pip_project.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendsControllerTest {

    @Mock
    private FriendsService friendsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendsController friendsController;

    private User user;
    private User friend;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        friend = new User();
        friend.setId(2L);
        friend.setEmail("friend@example.com");
    }

    @Test
    void addFriend_success() throws Exception {
        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setUser_id(user.getId());
        dto.setFriend_id(friend.getId());

        when(userService.findUserById(user.getId())).thenReturn(user);
        when(userService.findUserById(friend.getId())).thenReturn(friend);

        ResponseEntity<?> response = friendsController.addFriend(dto);

        verify(friendsService).sendFriendRequest(user, friend);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Friend request added", response.getBody());
    }

    @Test
    void addFriend_failure() throws Exception {
        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setUser_id(user.getId());
        dto.setFriend_id(friend.getId());

        when(userService.findUserById(user.getId())).thenReturn(user);
        when(userService.findUserById(friend.getId())).thenReturn(friend);
        doThrow(new IllegalStateException("Friend request already sent")).when(friendsService).sendFriendRequest(user, friend);

        ResponseEntity<?> response = friendsController.addFriend(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Friend request already sent"));
    }

    @Test
    void getAllFriends_nonEmptyList() throws Exception {
        when(userService.findUserById(user.getId())).thenReturn(user);
        when(friendsService.getAllFriends(user)).thenReturn(List.of(friend));

        ResponseEntity<?> response = friendsController.getAllFriends(user.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>)response.getBody()).size());
    }

    @Test
    void getAllFriends_emptyList() throws Exception {
        when(userService.findUserById(user.getId())).thenReturn(user);
        when(friendsService.getAllFriends(user)).thenReturn(List.of());

        ResponseEntity<?> response = friendsController.getAllFriends(user.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void unfriend_success() throws Exception {
        when(userService.findUserById(user.getId())).thenReturn(user);
        when(userService.findUserById(friend.getId())).thenReturn(friend);

        ResponseEntity<?> response = friendsController.unfriend(user.getId(), friend.getId());

        verify(friendsService).deleteFriend(user, friend);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Friend deleted successfully", response.getBody());
    }

    @Test
    void unfriend_failure() throws Exception {
        when(userService.findUserById(user.getId())).thenReturn(user);
        when(userService.findUserById(friend.getId())).thenReturn(friend);
        doThrow(new RuntimeException("Some error")).when(friendsService).deleteFriend(user, friend);

        ResponseEntity<?> response = friendsController.unfriend(user.getId(), friend.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Some error"));
    }
}
