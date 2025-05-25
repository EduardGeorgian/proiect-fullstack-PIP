package org.pipproject.pip_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a friendship relationship between two users.
 * Contains status to track friend request state.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Friends {

    /**
     * Unique identifier of the friendship relation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who sent or owns the friend relationship.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * User who is the friend.
     */
    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    /**
     * Status of the friend request (e.g., PENDING, ACCEPTED, REJECTED).
     */
    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;
}
