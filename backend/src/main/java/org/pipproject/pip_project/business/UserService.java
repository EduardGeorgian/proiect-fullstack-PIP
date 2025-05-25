package org.pipproject.pip_project.business;

import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviciu pentru gestionarea operațiunilor legate de utilizatori,
 * inclusiv crearea, căutarea și validarea utilizatorilor.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor pentru UserService.
     *
     * @param userRepository repository pentru entitatea User
     * @param passwordEncoder componentă pentru criptarea parolelor
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Adaugă un utilizator nou în sistem.
     * Parola este criptată înainte de a fi salvată.
     * Aruncă excepție dacă un utilizator cu emailul dat există deja.
     *
     * @param username numele utilizatorului
     * @param email emailul utilizatorului
     * @param password parola necriptată a utilizatorului
     * @return utilizatorul salvat
     * @throws Exception dacă există deja un utilizator cu același email
     */
    public User addUser(String username, String email, String password) throws Exception {
        User user = new User(username, email, this.passwordEncoder.encode(password));

        if (userRepository.existsByEmail(email))
            throw new Exception("user already exists");

        userRepository.save(user);
        return user;
    }

    /**
     * Caută un utilizator după id.
     * Aruncă excepție dacă utilizatorul nu este găsit.
     *
     * @param id id-ul utilizatorului
     * @return utilizatorul găsit
     * @throws Exception dacă utilizatorul nu este găsit
     */
    public User findUserById(Long id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty())
            throw new Exception("user not found");

        return userOptional.get();
    }

    /**
     * Caută un utilizator după email.
     * Aruncă excepție dacă utilizatorul nu este găsit.
     *
     * @param email emailul utilizatorului
     * @return utilizatorul găsit
     * @throws Exception dacă utilizatorul nu este găsit
     */
    public User findUserByEmail(String email) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty())
            throw new Exception("user not found");

        return userOptional.get();
    }

    /**
     * Validează credențialele unui utilizator.
     * Compară parola introdusă cu parola stocată criptată.
     *
     * @param email emailul utilizatorului
     * @param password parola necriptată introdusă
     * @return true dacă parolele corespund, altfel false
     * @throws Exception dacă utilizatorul nu este găsit
     */
    public boolean validateUserCredentials(String email, String password) throws Exception {
        User user = this.findUserByEmail(email);

        return passwordEncoder.matches(password, user.getPassword());
    }

    public List<User> findAllUsers(String userEmail) throws Exception {
        Optional<User> currentUser = userRepository.findByEmail(userEmail);
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> !user.getEmail().equals(userEmail))
                .toList();
    }

}
