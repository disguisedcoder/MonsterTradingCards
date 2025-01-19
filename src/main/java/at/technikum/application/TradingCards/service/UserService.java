package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.DTO.UserDTO;
import at.technikum.application.TradingCards.exception.AuthenticationFailedException;
import at.technikum.application.TradingCards.exception.UserNotFoundException;
import at.technikum.application.TradingCards.repository.UserDbRepository;
import at.technikum.application.TradingCards.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new User, saves it to the repository, and returns a UserDTO.
     */
    public UserDTO createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        System.out.println("Registering user: " + user.getUsername());

        // Speichern des Benutzers in der Datenbank
        userRepository.save(user);

        // Konvertiere User zu UserDTO und gib es zurück
        return new UserDTO(
                user.getUsername(),
                user.getPassword(),
                user.getElo(),
                user.getCoins()
        );
    }

    /**
     * Authenticates a user by their username and password and generates a token.
     */
    public String authenticate(UserDTO userDTO) {
        System.out.println(userDTO.getUsername());

        // Benutzer anhand des Benutzernamens aus der Datenbank abrufen
        User user = userRepository.findByUsername(userDTO.getUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        // Passwort überprüfen
        if (!user.getPassword().equals(userDTO.getPassword())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        // Token zurückgeben
        return user.getToken();
    }

    public String validateToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Unauthorized access: Invalid authorization header.");
        }

        String token = authorizationHeader.substring(7); // Extrahiere den Token
        var user = userRepository.findByToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Unauthorized access: Invalid token.");
        }

        return token;
    }
    public void validateAdmin(String authorizationHeader) {
        String token = validateToken(authorizationHeader);
        var user = userRepository.findByToken(token);

        if (!"admin".equals(user.getUsername())) {
            throw new IllegalArgumentException("Unauthorized access: Admin rights required.");
        }
    }

    public UserDTO getUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + username);
        }
        return new UserDTO(
                user.getName(),
                user.getBio(),
                user.getImage()
        );
    }
    public void updateUser(String username, UserDTO updatedUserData) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            throw new UserNotFoundException("User not found: " + username);
        }

        // Aktualisiere die Werte im bestehenden Benutzerobjekt
        existingUser.setName(updatedUserData.getName());
        existingUser.setBio(updatedUserData.getBio());
        existingUser.setImage(updatedUserData.getImage());

        // Speichere die Änderungen in der Datenbank
        userRepository.updateUserDetails(username, updatedUserData.getName(), updatedUserData.getBio(), updatedUserData.getImage());
    }

    public void validateAccess(String token, String targetUsername) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid token.");
        }

        if (!user.getUsername().equals(targetUsername)) {
            throw new IllegalArgumentException("Access denied: You are not authorized to access this resource.");
        }
    }
}
