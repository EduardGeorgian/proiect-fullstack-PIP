package org.pipproject.pip_project.business;


import org.pipproject.pip_project.model.FriendRequestStatus;
import org.pipproject.pip_project.model.Friends;
import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.FriendsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendsService {
    private final FriendsRepository friendsRepository;

    @Autowired
    public FriendsService(final FriendsRepository friendsRepository) {
        this.friendsRepository = friendsRepository;
    }

    public void sendFriendRequest(User user, User friend) {
        boolean alreadyRequested = friendsRepository.findByUserAndFriend(user,friend).isPresent();
        if (alreadyRequested && friendsRepository.findByUserAndFriend(user,friend).get().getStatus()==FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Friend request already sent");
        }else if(alreadyRequested && friendsRepository.findByUserAndFriend(user,friend).get().getStatus()==FriendRequestStatus.ACCEPTED) {
            throw new IllegalStateException("Already accepted");
        }
        Friends friendRequest = new Friends();
        friendRequest.setUser(user);
        friendRequest.setFriend(friend);
        friendRequest.setStatus(FriendRequestStatus.PENDING);
        friendsRepository.save(friendRequest);
    }

    public List<User> getAllFriends(User user) {
        List<Friends> asUser = friendsRepository.findAllByUserAndStatus(Optional.ofNullable(user), FriendRequestStatus.ACCEPTED);
        List<Friends> asFriend = friendsRepository.findAllByFriendAndStatus(Optional.ofNullable(user), FriendRequestStatus.ACCEPTED);

        List<User> friends = new ArrayList<>();
        asUser.forEach(f -> friends.add(f.getFriend()));
        asFriend.forEach(f -> friends.add(f.getUser()));

        return friends;

    }

}
