package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.packages.Package;
import at.technikum.application.data.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageDbRepository implements PackageRepository {

    private static final String INSERT_PACKAGE = "INSERT INTO packages (acquired_by) VALUES (NULL)";
    private static final String SELECT_PACKAGE = "SELECT * FROM packages WHERE acquired_by IS NULL ORDER BY id LIMIT 1"; // ORDER BY RANDOM() -> für random
    private static final String UPDATE_PACKAGE_ACQUIRED_BY = "UPDATE packages SET acquired_by = ? WHERE id = ?";
    private static final String SELECT_CARDS_IN_PACKAGE = "SELECT * FROM cards WHERE package_id = ?";
    private static final String UPDATE_CARDS_WITH_USERNAME = "UPDATE cards SET username = ? WHERE package_id = ?";
    private static final String CHECK_AVAILABLE_PACKAGES = "SELECT COUNT(*) AS count FROM packages WHERE acquired_by IS NULL";

    private final ConnectionPool connectionPool;

    public PackageDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public int save() {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_PACKAGE, Statement.RETURN_GENERATED_KEYS)) {
            statement.executeUpdate();
            return getGeneratedPackageId(statement); // Fetch the package ID
        } catch (SQLException e) {
            throw new RuntimeException("Error saving package", e);
        }
    }

    private int getGeneratedPackageId(PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Retrieve the generated package ID
            } else {
                throw new SQLException("Failed to retrieve generated package ID.");
            }
        }
    }

    @Override
    public Package acquirePackage(String username) {
        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            int packageId;

            // 1. Fetch a random available package
            try (PreparedStatement statement = connection.prepareStatement(SELECT_PACKAGE)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        packageId = resultSet.getInt("id");
                    } else {
                        return null; // No package available
                    }
                }
            }

            // 2. Mark the package as acquired
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_PACKAGE_ACQUIRED_BY)) {
                statement.setString(1, username);
                statement.setInt(2, packageId);
                statement.executeUpdate();
            }

            // 3. Update cards with the username
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_CARDS_WITH_USERNAME)) {
                statement.setString(1, username);
                statement.setInt(2, packageId);
                statement.executeUpdate();
            }

            // 4. Retrieve cards in the package
            List<Card> cards = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(SELECT_CARDS_IN_PACKAGE)) {
                statement.setInt(1, packageId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        cards.add(new Card(
                                resultSet.getString("id"),
                                resultSet.getString("name"),
                                resultSet.getDouble("damage")
                        ));
                    }
                }
            }

            connection.commit(); // Commit transaction

            // 5. Return the package
            return new Package(packageId, cards);

        } catch (SQLException e) {
            throw new RuntimeException("Error acquiring package", e);
        }
    }

    @Override
    public boolean hasPackages() {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(CHECK_AVAILABLE_PACKAGES);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking for packages", e);
        }
        return false;
    }
}
