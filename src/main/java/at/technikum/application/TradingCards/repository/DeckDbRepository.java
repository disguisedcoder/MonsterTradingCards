package at.technikum.application.TradingCards.repository;

import at.technikum.application.data.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeckDbRepository implements DeckRepository {
    private final ConnectionPool connectionPool;

    private static final String DELETE_DECK = "DELETE FROM decks WHERE username = ?";
    private static final String INSERT_DECK = "INSERT INTO decks (username, card_id) VALUES (?, ?)";
    private static final String SELECT_DECK = "SELECT card_id FROM decks WHERE username = ?";
    private static final String SELECT_USERNAME_FROM_TOKEN = "SELECT username FROM users WHERE token = ?";
    private static final String CHECK_DECK = "SELECT 1 FROM decks WHERE username = ? LIMIT 1";
    private static final String GET_PLAINDECK = "SELECT c.id, c.name, c.damage FROM decks d INNER JOIN cards c ON d.card_id = c.id WHERE d.username = ?";

    public DeckDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void clearDeck(String username) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_DECK)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error clearing deck for user: " + username, e);
        }
    }

    @Override
    public void addCardsToDeck(String username, List<String> cardIds) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_DECK)) {

            for (String cardId : cardIds) {
                statement.setString(1, username);
                statement.setString(2, cardId);
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding cards to deck for user: " + username, e);
        }
    }

    @Override
    public List<String> getDeck(String username) {
        List<String> deck = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_DECK)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    deck.add(resultSet.getString("card_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving deck for user: " + username, e);
        }

        return deck;
    }

    @Override
    public List<String> getPlainDeck(String username) {
        List<String> plainDeck = new ArrayList<>();


        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_PLAINDECK)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String cardDetails = String.format(
                            "ID: %s, Name: %s, Damage: %.2f",
                            resultSet.getString("id"),
                            resultSet.getString("name"),
                            resultSet.getDouble("damage")
                    );
                    plainDeck.add(cardDetails);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving plain deck for user: " + username, e);
        }

        return plainDeck;
    }



    @Override
    public boolean hasDeck(String username) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(CHECK_DECK)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking deck for user: " + username, e);
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USERNAME_FROM_TOKEN)) {
            statement.setString(1, token);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
                throw new RuntimeException("Invalid token: User not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving username from token.", e);
        }
    }
}
