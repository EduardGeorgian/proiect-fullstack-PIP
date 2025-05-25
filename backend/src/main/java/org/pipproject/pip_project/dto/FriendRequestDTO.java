package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for sending a friend request.
 * Contains the IDs of the user sending the request and the user to be added as a friend.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDTO {

    /**
     * The ID of the user who sends the friend request.
     */
    private long user_id;

    /**
     * The ID of the user to be added as a friend.
     */
    private long friend_id;
}
