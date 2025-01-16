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
}
