package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.data.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDbRepository implements UserRepository {

    private static final String INSERT_USER =
            "INSERT INTO users (username, password, token, coins, elo) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_USER_BY_USERNAME =
            "SELECT * FROM users WHERE username = ?";
    private static final String SELECT_USER_BY_TOKEN =
            "SELECT * FROM users WHERE token = ?";
    private static final String SELECT_ALL_USERS =
            "SELECT * FROM users";
    private static final String DELETE_USER =
            "DELETE FROM users WHERE username = ?";

    private final ConnectionPool connectionPool;

    public UserDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public User save(User user) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)
        ) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getToken());
            preparedStatement.setInt(4, user.getCoins());
            preparedStatement.setInt(5, user.getElo());
            preparedStatement.execute();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving user", e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(mapToUser(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all users", e);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME)
        ) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding user by username", e);
        }
        return null;
    }

    @Override
    public User findByToken(String token) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_TOKEN)
        ) {
            preparedStatement.setString(1, token);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding user by token", e);
        }
        return null;
    }

    @Override
    public boolean delete(User user) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)
        ) {
            preparedStatement.setString(1, user.getUsername());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Gibt true zurück, wenn mindestens eine Zeile betroffen ist
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting user", e);
        }
    }

    /**
     * Mappt ein `ResultSet` zu einem `User`-Objekt.
     */
    private User mapToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setToken(resultSet.getString("token"));
        user.setCoins(resultSet.getInt("coins"));
        user.setElo(resultSet.getInt("elo"));
        return user;
    }
}
