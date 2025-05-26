package org.pipproject.pip_project.repositories;


import org.pipproject.pip_project.model.FriendRequestStatus;
import org.pipproject.pip_project.model.Friends;
import org.pipproject.pip_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
    Optional<Friends> findByUserAndFriend(User user, User friend);
    List<Friends> findAllByUserAndStatus(Optional<User> user, FriendRequestStatus status);
    List<Friends> findAllByFriendAndStatus(Optional<User> user, FriendRequestStatus status);
    @Query("SELECT f FROM Friends f WHERE " +
            "(f.user = :user1 AND f.friend = :user2) OR " +
            "(f.user = :user2 AND f.friend = :user1)")
    Optional<Friends> findByUsers(@Param("user1") User user1, @Param("user2") User user2);

}
