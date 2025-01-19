package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.entity.user.User;

import java.util.List;

public interface UserRepository {

    User save(User user);

    List<User> findAll();

    User findByUsername(String username);

    User findByToken(String token);

    boolean delete(User user);

    boolean update(User user); // New method for updating users

    boolean updateUserDetails(String username, String name, String bio, String image);


}
