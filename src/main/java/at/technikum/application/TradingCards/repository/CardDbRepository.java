package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.data.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardDbRepository implements CardRepository {
    private final ConnectionPool connectionPool;

    private static final String INSERT_CARD = "INSERT INTO cards (id, name, damage, element, ismonster, isspell, monstertype, package_id, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_CARD_BY_ID = "SELECT * FROM cards WHERE id = ?";
    private static final String SELECT_ALL_CARDS = "SELECT * FROM cards";
    private static final String SELECT_CARDS_BY_PACKAGE_ID = "SELECT * FROM cards WHERE package_id = ?";
    private static final String SELECT_CARDS_BY_USERNAME = "SELECT * FROM cards WHERE username = ?";
    private static final String SELECT_DECK = "SELECT * FROM cards WHERE id IN (SELECT card_id FROM decks WHERE username = ?)";
    private static final String INSERT_DECK = "INSERT INTO decks (username, card_id) VALUES (?, ?)";
    private static final String DELETE_DECK = "SELECT * FROM decks WHERE username = ?";



    public CardDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void save(Card card) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CARD)
        ) {
            preparedStatement.setString(1, card.getId());
            preparedStatement.setString(2, card.getName());
            preparedStatement.setDouble(3, card.getDamage());
            preparedStatement.setString(4, card.getElement().name());
            preparedStatement.setBoolean(5, card.isMonster());
            preparedStatement.setBoolean(6, card.isSpell());
            preparedStatement.setString(7, card.getMonsterType() != null ? card.getMonsterType().name() : null);
            preparedStatement.setInt(8, card.getPackage_id()); // Standardmäßig ist die Karte nicht Teil eines Pakets
            preparedStatement.setString(9, card.getUsername()); // Standardmäßig hat die Karte keinen Besitzer
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving card: " + e.getMessage(), e);
        }
    }

    public List<Card> findByPackageId(int packageId) {
        List<Card> cards = new ArrayList<>();
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CARDS_BY_PACKAGE_ID)
        ) {
            preparedStatement.setInt(1, packageId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cards.add(mapToCard(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding cards by package ID: " + e.getMessage(), e);
        }
        return cards;
    }

    public List<Card> findByUsername(String username) {
        List<Card> cards = new ArrayList<>();
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CARDS_BY_USERNAME)
        ) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cards.add(mapToCard(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding cards by username: " + e.getMessage(), e);
        }
        return cards;
    }

    @Override
    public Card findById(String cardId) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CARD_BY_ID)
        ) {
            preparedStatement.setString(1, cardId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToCard(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding card by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Card> findAll() {
        List<Card> cards = new ArrayList<>();
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_CARDS);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                cards.add(mapToCard(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all cards: " + e.getMessage(), e);
        }
        return cards;
    }

    private Card mapToCard(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String name = resultSet.getString("name");
        double damage = resultSet.getDouble("damage");
        int package_id = resultSet.getInt("package_id");
        String username = resultSet.getString("username");
        // Die `Card`-Klasse bestimmt automatisch `element`, `isMonster`, `isSpell`, und `monsterType`
        Card newCard = new Card(id, name, damage);
        newCard.setPackage_id(package_id);
        newCard.setUsername(username);
        return newCard;
    }


    @Override
    public List<Card> getDeck(String username) {
        List<Card> deck = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DECK)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    deck.add(mapToCard(resultSet)); // Verwende mapToCard, um jede Karte zu verarbeiten
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving deck for user: " + username, e);
        }

        return deck;
    }



}
