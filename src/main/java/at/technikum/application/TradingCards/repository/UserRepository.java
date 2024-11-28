package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.DTO.UserDTO;
import at.technikum.application.TradingCards.entity.user.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getUsername(), user);
    }

    public User findByUsername(String username) {
        return users.get(username);
    }

}
