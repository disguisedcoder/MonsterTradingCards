package at.technikum.application.data;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseCleaner {

    private final ConnectionPool connectionPool;

    public DatabaseCleaner(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void clearAllTables() {
        String[] truncateStatements = {
                "TRUNCATE TABLE tradings CASCADE",
                "TRUNCATE TABLE decks CASCADE",
                "TRUNCATE TABLE stacks CASCADE",
                "TRUNCATE TABLE cards CASCADE",
                "TRUNCATE TABLE packages CASCADE",
                "TRUNCATE TABLE users CASCADE"
        };

        String[] resetSequences = {
                "ALTER SEQUENCE packages_id_seq RESTART WITH 1"
        };

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            // Truncate all tables
            for (String truncate : truncateStatements) {
                statement.executeUpdate(truncate);
            }

            // Reset sequences for auto-increment fields
            for (String reset : resetSequences) {
                statement.executeUpdate(reset);
            }

            System.out.println("All tables cleared and sequences reset successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Error clearing tables or resetting sequences: " + e.getMessage(), e);
        }
    }
}
