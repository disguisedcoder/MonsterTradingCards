package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.DTO.UserDTO;
import at.technikum.application.TradingCards.exception.AuthenticationFailedException;
import at.technikum.application.TradingCards.exception.UserNotFoundException;
import at.technikum.application.TradingCards.repository.UserRepository;

import java.util.Collection;


public class UserService {
    private final UserRepository userRepository = new UserRepository();

    /**
     * Creates a new User, saves it to the repository, and returns a UserDTO.
     */
    public UserDTO createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        System.out.println("Registering user: " + user.getUsername());
        userRepository.save(user);


        // Convert User to UserDTO and return
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

        User user = userRepository.findByUsername(userDTO.getUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (!user.getPassword().equals(userDTO.getPassword())) {
            throw new  AuthenticationFailedException("Invalid credentials");
        }
        return user.getToken();
    }
}
