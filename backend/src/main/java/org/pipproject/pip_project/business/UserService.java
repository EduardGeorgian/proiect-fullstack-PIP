package org.pipproject.pip_project.business;

import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(String username, String email, String password) throws Exception {
        User user = new User(username, email, this.passwordEncoder.encode(password));

        if (userRepository.existsByEmail(email))
            throw new Exception("user already exists");

        userRepository.save(user);
        return user;
    }

    public User findUserById(Long id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty())
            throw new Exception("user not found");

        return userOptional.get();
    }

    private User findUserByEmail(String email) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty())
            throw new Exception("user not found");

        return userOptional.get();
    }

    public boolean validateUserCredentials(String email, String password) throws Exception {
        User user = this.findUserByEmail(email);

        return passwordEncoder.matches(password, user.getPassword());
    }

}
