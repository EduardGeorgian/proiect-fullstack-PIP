package org.pipproject.pip_project.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUser_success() throws Exception {
        String username = "testUser";
        String email = "test@example.com";
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.addUser(username, email, rawPassword);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(encodedPassword, result.getPassword());

        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_existingEmail_throwsException() {
        String email = "exists@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.addUser("any", email, "pass");
        });

        assertEquals("user already exists", exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserById_found() throws Exception {
        User user = new User("u", "email@example.com", "pass");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findUserById(1L);
        assertEquals(user, result);
    }

    @Test
    void findUserById_notFound_throwsException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> userService.findUserById(2L));
        assertEquals("user not found", ex.getMessage());
    }

    @Test
    void validateUserCredentials_correctPassword_returnsTrue() throws Exception {
        String rawPassword = "raw";
        String encodedPassword = "encoded";

        User user = new User("u", "email@example.com", encodedPassword);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        assertTrue(userService.validateUserCredentials(user.getEmail(), rawPassword));
    }

    @Test
    void validateUserCredentials_wrongPassword_returnsFalse() throws Exception {
        String rawPassword = "raw";
        String encodedPassword = "encoded";

        User user = new User("u", "email@example.com", encodedPassword);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        assertFalse(userService.validateUserCredentials(user.getEmail(), rawPassword));
    }
}
