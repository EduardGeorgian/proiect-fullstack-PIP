package org.pipproject.pip_project.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.model.FriendRequestStatus;
import org.pipproject.pip_project.model.Friends;
import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.FriendsRepository;
import org.pipproject.pip_project.repositories.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendsServiceTest {

    @Mock
    private FriendsRepository friendsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendsService friendsService;

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
    void sendFriendRequest_success() {
        when(friendsRepository.findByUserAndFriend(user, friend)).thenReturn(Optional.empty());

        friendsService.sendFriendRequest(user, friend);

        ArgumentCaptor<Friends> captor = ArgumentCaptor.forClass(Friends.class);
        verify(friendsRepository).save(captor.capture());
        assertEquals(FriendRequestStatus.PENDING, captor.getValue().getStatus());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(friend, captor.getValue().getFriend());
    }

    @Test
    void sendFriendRequest_alreadyPending_throws() {
        Friends existingRequest = new Friends();
        existingRequest.setStatus(FriendRequestStatus.PENDING);

        when(friendsRepository.findByUserAndFriend(user, friend)).thenReturn(Optional.of(existingRequest));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> friendsService.sendFriendRequest(user, friend));
        assertEquals("Friend request already sent", ex.getMessage());
    }

    @Test
    void sendFriendRequest_alreadyAccepted_throws() {
        Friends existingRequest = new Friends();
        existingRequest.setStatus(FriendRequestStatus.ACCEPTED);

        when(friendsRepository.findByUserAndFriend(user, friend)).thenReturn(Optional.of(existingRequest));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> friendsService.sendFriendRequest(user, friend));
        assertEquals("Already accepted", ex.getMessage());
    }

    @Test
    void getAllFriends_returnsFriends() {
        Friends f1 = new Friends();
        f1.setFriend(friend);
        f1.setStatus(FriendRequestStatus.ACCEPTED);

        Friends f2 = new Friends();
        f2.setUser(friend);
        f2.setStatus(FriendRequestStatus.ACCEPTED);

        when(friendsRepository.findAllByUserAndStatus(Optional.ofNullable(user), FriendRequestStatus.ACCEPTED)).thenReturn(List.of(f1));
        when(friendsRepository.findAllByFriendAndStatus(Optional.ofNullable(user), FriendRequestStatus.ACCEPTED)).thenReturn(List.of(f2));

        List<User> friends = friendsService.getAllFriends(user);

        assertEquals(2, friends.size());
        assertTrue(friends.contains(friend));
    }

    @Test
    void deleteFriend_callsRepositoryDelete() {
        Friends friendship = new Friends();
        friendship.setUser(user);
        friendship.setFriend(friend);

        when(friendsRepository.findByUserAndFriend(user, friend)).thenReturn(Optional.of(friendship));

        friendsService.deleteFriend(user, friend);

        verify(friendsRepository).delete(friendship);
    }
}
