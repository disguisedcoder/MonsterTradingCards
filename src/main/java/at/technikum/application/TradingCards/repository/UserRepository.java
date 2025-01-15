package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.DTO.UserDTO;
import at.technikum.application.TradingCards.entity.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getUsername(), user);

    }

    public User findByUsername(String username) {
        System.out.println((username));
        return users.get(username);
    }
    // Methode zum Abrufen aller Benutzer
    public Collection<User> findAll() {
        return users.values(); // Gibt alle Benutzer als Collection zurück
    }
}
