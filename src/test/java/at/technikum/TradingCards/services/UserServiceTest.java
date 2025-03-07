package at.technikum.TradingCards.services;

import at.technikum.application.TradingCards.DTO.UserDTO;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.exception.AuthenticationFailedException;
import at.technikum.application.TradingCards.exception.UserNotFoundException;
import at.technikum.application.TradingCards.repository.StatsRepository;
import at.technikum.application.TradingCards.repository.UserRepository;
import at.technikum.application.TradingCards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private StatsRepository statsRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        statsRepository = mock(StatsRepository.class);
        userService = new UserService(userRepository, statsRepository);
    }

    @Test
    void createUser_ShouldCreateAndReturnUserDTO_WhenUserDoesNotExist() {
        // Arrange
        User newUser = new User("newUser", "password");
        when(userRepository.findByUsername("newUser")).thenReturn(null);

        // Act
        UserDTO result = userService.createUser(newUser);

        // Assert
        assertEquals("newUser", result.getUsername());
        assertEquals("password", result.getPassword());
        verify(userRepository, times(1)).save(newUser);
        // Verify that stats creation is invoked for non-admin users
        verify(statsRepository, times(1)).createStats("newUser");
    }

    @Test
    void createUser_ShouldNotCallCreateStats_WhenUserIsAdmin() {
        // Arrange
        User adminUser = new User("admin", "adminPass");
        when(userRepository.findByUsername("admin")).thenReturn(null);

        // Act
        UserDTO result = userService.createUser(adminUser);

        // Assert
        assertEquals("admin", result.getUsername());
        assertEquals("adminPass", result.getPassword());
        verify(userRepository, times(1)).save(adminUser);
        // Admin should not trigger stats creation
        verify(statsRepository, never()).createStats(anyString());
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        User existingUser = new User("existingUser", "password");
        when(userRepository.findByUsername("existingUser")).thenReturn(existingUser);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(existingUser)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        UserDTO userDTO = new UserDTO("testUser", "testPass", 0, 0);  // (username, password, elo, coins)
        User mockUser = new User("testUser", "testPass");
        mockUser.setToken("validToken");
        when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        // Act
        String token = userService.authenticate(userDTO);

        // Assert
        assertEquals("validToken", token);
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void authenticate_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        UserDTO userDTO = new UserDTO("notFound", "somePass", 0, 0);
        when(userRepository.findByUsername("notFound")).thenReturn(null);

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.authenticate(userDTO)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void authenticate_ShouldThrowAuthenticationFailedException_WhenPasswordIsWrong() {
        // Arrange
        UserDTO userDTO = new UserDTO("testUser", "wrongPass", 0, 0);
        User mockUser = new User("testUser", "correctPass");
        when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        // Act & Assert
        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> userService.authenticate(userDTO)
        );
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void validateToken_ShouldReturnToken_WhenHeaderIsValidAndUserExists() {
        // Arrange
        String header = "Bearer valid-token";
        User mockUser = new User("someUser", "somePass");
        when(userRepository.findByToken("valid-token")).thenReturn(mockUser);

        // Act
        String resultToken = userService.validateToken(header);

        // Assert
        assertEquals("valid-token", resultToken);
        verify(userRepository, times(1)).findByToken("valid-token");
    }

    @Test
    void validateToken_ShouldThrowException_WhenHeaderIsNullOrMissingBearer() {
        // Arrange
        String invalidHeader1 = null;
        String invalidHeader2 = "Basic something";

        // Act & Assert
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> userService.validateToken(invalidHeader1)
        );
        assertTrue(ex1.getMessage().contains("Invalid authorization header"));

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> userService.validateToken(invalidHeader2)
        );
        assertTrue(ex2.getMessage().contains("Invalid authorization header"));
    }

    @Test
    void validateToken_ShouldThrowException_WhenUserIsNull() {
        // Arrange
        String header = "Bearer invalid-token";
        when(userRepository.findByToken("invalid-token")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.validateToken(header)
        );
        assertTrue(ex.getMessage().contains("Invalid token"));
    }

    @Test
    void validateAdmin_ShouldNotThrow_WhenUserIsAdmin() {
        // Arrange
        String header = "Bearer admin-token";
        User adminUser = new User("admin", "pass");
        when(userRepository.findByToken("admin-token")).thenReturn(adminUser);

        // Act & Assert
        assertDoesNotThrow(() -> userService.validateAdmin(header));
    }

    @Test
    void validateAdmin_ShouldThrow_WhenUserIsNotAdmin() {
        // Arrange
        String header = "Bearer some-token";
        User normalUser = new User("someUser", "pass");
        when(userRepository.findByToken("some-token")).thenReturn(normalUser);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.validateAdmin(header)
        );
        assertEquals("Unauthorized access: Admin rights required.", ex.getMessage());
    }

    @Test
    void getUser_ShouldReturnUserDTO_WhenUserExists() {
        // Arrange
        User mockUser = new User("testUser", "pass");
        mockUser.setName("Test Name");
        mockUser.setBio("Test Bio");
        mockUser.setImage("Test Image");
        when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        // Act
        UserDTO result = userService.getUser("testUser");

        // Assert
        assertEquals("Test Name", result.getName());
        assertEquals("Test Bio", result.getBio());
        assertEquals("Test Image", result.getImage());
    }

    @Test
    void getUser_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("notFound")).thenReturn(null);

        // Act & Assert
        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUser("notFound")
        );
        assertTrue(ex.getMessage().contains("User not found: notFound"));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        // Arrange
        User existingUser = new User("testUser", "pass");
        when(userRepository.findByUsername("testUser")).thenReturn(existingUser);

        UserDTO updated = new UserDTO();  // Assuming a no-arg constructor exists
        updated.setName("New Name");
        updated.setBio("New Bio");
        updated.setImage("New Image");

        // Act
        userService.updateUser("testUser", updated);

        // Assert
        verify(userRepository, times(1))
                .updateUserDetails("testUser", "New Name", "New Bio", "New Image");
        assertEquals("New Name", existingUser.getName());
        assertEquals("New Bio", existingUser.getBio());
        assertEquals("New Image", existingUser.getImage());
    }

    @Test
    void updateUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);
        UserDTO updated = new UserDTO();

        // Act & Assert
        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser("unknownUser", updated)
        );
        assertTrue(ex.getMessage().contains("User not found: unknownUser"));
        verify(userRepository, never()).updateUserDetails(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void validateAccess_ShouldNotThrow_WhenTokenValidAndUsernameMatches() {
        // Arrange
        User mockUser = new User("someUser", "pass");
        when(userRepository.findByToken("validToken")).thenReturn(mockUser);

        // Act & Assert
        assertDoesNotThrow(() -> userService.validateAccess("validToken", "someUser"));
    }

    @Test
    void validateAccess_ShouldThrow_WhenTokenIsInvalid() {
        // Arrange
        when(userRepository.findByToken("invalidToken")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.validateAccess("invalidToken", "someUser")
        );
        assertEquals("Invalid token.", ex.getMessage());
    }

    @Test
    void validateAccess_ShouldThrow_WhenUserTokenDoesNotMatchTargetUsername() {
        // Arrange
        User mockUser = new User("otherUser", "pass");
        when(userRepository.findByToken("validToken")).thenReturn(mockUser);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.validateAccess("validToken", "someUser")
        );
        assertTrue(ex.getMessage().contains("Access denied"));
    }
}
