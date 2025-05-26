package org.pipproject.pip_project.business;

import org.pipproject.pip_project.model.FriendRequestStatus;
import org.pipproject.pip_project.model.Friends;
import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.FriendsRepository;
import org.pipproject.pip_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pentru gestionarea prieteniilor între utilizatori.
 * Oferă funcționalități pentru trimiterea cererilor de prietenie,
 * obținerea listei de prieteni și ștergerea unui prieten.
 */
@Service
public class FriendsService {
    private final FriendsRepository friendsRepository;
    private final UserRepository userRepository;

    /**
     * Constructor pentru FriendsService.
     *
     * @param friendsRepository repository pentru entitatea Friends
     * @param userRepository repository pentru entitatea User
     */
    @Autowired
    public FriendsService(final FriendsRepository friendsRepository, UserRepository userRepository) {
        this.friendsRepository = friendsRepository;
        this.userRepository = userRepository;
    }

    /**
     * Trimite o cerere de prietenie de la un utilizator către altul.
     * Aruncă excepție dacă cererea a fost deja trimisă sau dacă utilizatorii sunt deja prieteni.
     *
     * @param user utilizatorul care trimite cererea
     * @param friend utilizatorul către care se trimite cererea
     * @throws IllegalStateException dacă cererea este deja trimisă sau prietenia este acceptată
     */
    public void sendFriendRequest(User user, User friend) {
        Optional<Friends> existing = friendsRepository
                .findByUsers(user, friend);

        if (existing.isPresent()) {
            FriendRequestStatus status = existing.get().getStatus();

            if (status == FriendRequestStatus.PENDING) {
                throw new IllegalStateException("Friend request already sent or received");
            } else if (status == FriendRequestStatus.ACCEPTED) {
                throw new IllegalStateException("You are already friends");
            }
        }

        // Nu exista cerere, creez una noua
        Friends friendRequest = new Friends();
        friendRequest.setUser(user);
        friendRequest.setFriend(friend);
        friendRequest.setStatus(FriendRequestStatus.PENDING);
        friendsRepository.save(friendRequest);
    }


    /**
     * Obține lista tuturor prietenilor unui utilizator.
     * Caută atât relațiile unde utilizatorul este inițiatorul cât și cele unde este destinat.
     *
     * @param user utilizatorul pentru care se obțin prietenii
     * @return listă de utilizatori care sunt prieteni cu utilizatorul dat
     */
    public List<User> getAllFriends(User user) {
        List<Friends> asUser = friendsRepository.findAllByUserAndStatus(Optional.ofNullable(user), FriendRequestStatus.ACCEPTED);
        List<Friends> asFriend = friendsRepository.findAllByFriendAndStatus(Optional.ofNullable(user), FriendRequestStatus.ACCEPTED);

        List<User> friends = new ArrayList<>();
        asUser.forEach(f -> friends.add(f.getFriend()));
        asFriend.forEach(f -> friends.add(f.getUser()));

        return Set.copyOf(friends).stream().toList();
    }

    /**
     * Șterge un prieten din lista de prieteni a unui utilizator.
     *
     * @param user utilizatorul care dorește să elimine prietenia
     * @param friend utilizatorul care va fi eliminat din lista de prieteni
     */
    public void deleteFriend(User user, User friend) {
        Optional<Friends> friendEntryOpt = friendsRepository.findByUsers(user, friend); // vezi mai jos metoda

        if (friendEntryOpt.isEmpty()) {
            throw new IllegalStateException("Friendship not found");
        }

        friendsRepository.delete(friendEntryOpt.get());
    }


    public List<User> getReceivedFriendRequests(User user) {
        List<Friends> receivedRequests = friendsRepository.findAllByFriendAndStatus(Optional.ofNullable(user), FriendRequestStatus.PENDING);
        // Extragem doar utilizatorii care au trimis cererea
        return receivedRequests.stream()
                .map(Friends::getUser) // user = cel care a trimis cererea
                .collect(Collectors.toList());
    }

    public void acceptFriendRequest(User currentUser, User sender) {
        Optional<Friends> friendRequestOpt = friendsRepository
                .findByUserAndFriend(sender, currentUser); // invers: sender → currentUser

        if (friendRequestOpt.isEmpty()) {
            throw new IllegalStateException("Friend request not found");
        }

        Friends friendRequest = friendRequestOpt.get();

        if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }

        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        friendsRepository.save(friendRequest);
    }

    public void rejectFriendRequest(User currentUser, User sender) {
        Optional<Friends> friendRequestOpt = friendsRepository
                .findByUserAndFriend(sender, currentUser); // cererea e de la sender → currentUser

        if (friendRequestOpt.isEmpty()) {
            throw new IllegalStateException("Friend request not found");
        }

        Friends friendRequest = friendRequestOpt.get();

        if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }

        friendRequest.setStatus(FriendRequestStatus.REJECTED);
        friendsRepository.save(friendRequest);
    }



}
