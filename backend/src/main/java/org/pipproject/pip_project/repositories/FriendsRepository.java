package org.pipproject.pip_project.repositories;


import org.pipproject.pip_project.model.FriendRequestStatus;
import org.pipproject.pip_project.model.Friends;
import org.pipproject.pip_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
    Optional<Friends> findByUserAndFriend(User user, User friend);
    List<Friends> findAllByUserAndStatus(Optional<User> user, FriendRequestStatus status);
    List<Friends> findAllByFriendAndStatus(Optional<User> user, FriendRequestStatus status);

}
