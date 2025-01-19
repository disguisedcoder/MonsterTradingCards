package at.technikum.TradingCards.services;

import at.technikum.application.TradingCards.DTO.UserDTO;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.exception.AuthenticationFailedException;
import at.technikum.application.TradingCards.exception.UserNotFoundException;
import at.technikum.application.TradingCards.repository.UserRepository;
import at.technikum.application.TradingCards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void createUser_ShouldCreateNewUser() {
        // Arrange
        User user = new User("testUser", "testPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        // Act
        UserDTO createdUser = userService.createUser(user);

        // Assert
        assertEquals("testUser", createdUser.getUsername());
        assertEquals("testPassword", createdUser.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_ShouldThrowException_WhenUserAlreadyExists() {
        // Arrange
        User user = new User("testUser", "testPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        User user = new User("testUser", "testPassword");
        user.setToken("validToken");
        UserDTO userDTO = new UserDTO("testUser", "testPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        // Act
        String token = userService.authenticate(userDTO);

        // Assert
        assertEquals("validToken", token);
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        UserDTO userDTO = new UserDTO("testUser", "testPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.authenticate(userDTO));
    }

    @Test
    void authenticate_ShouldThrowException_WhenPasswordIsInvalid() {
        // Arrange
        User user = new User("testUser", "correctPassword");
        UserDTO userDTO = new UserDTO("testUser", "wrongPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        // Act & Assert
        assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(userDTO));
    }

    @Test
    void validateToken_ShouldReturnToken_WhenValid() {
        // Arrange
        User user = new User("testUser", "testPassword");
        user.setToken("validToken");
        when(userRepository.findByToken("validToken")).thenReturn(user);

        // Act
        String token = userService.validateToken("Bearer validToken");

        // Assert
        assertEquals("validToken", token);
    }

    @Test
    void validateToken_ShouldThrowException_WhenTokenIsInvalid() {
        // Arrange
        when(userRepository.findByToken("invalidToken")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.validateToken("Bearer invalidToken"));
    }

    @Test
    void validateAdmin_ShouldThrowException_WhenUserIsNotAdmin() {
        // Arrange
        User user = new User("testUser", "testPassword");
        user.setToken("validToken");
        when(userRepository.findByToken("validToken")).thenReturn(user);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.validateAdmin("Bearer validToken"));
    }

    @Test
    void getUs
