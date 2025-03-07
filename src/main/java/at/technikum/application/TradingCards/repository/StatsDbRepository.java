package at.technikum.application.TradingCards.repository;

import at.technikum.application.data.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StatsDbRepository implements StatsRepository {

    private final ConnectionPool connectionPool;

    private static final String INSERT_STATS = "INSERT INTO stats (username, wins, losses, elo) VALUES (?, 0, 0, 100)";
    private static final String ADD_WIN = "UPDATE stats SET wins = wins + 1 WHERE username = ?";
    private static final String ADD_LOSS = "UPDATE stats SET losses = losses + 1 WHERE username = ?";
    private static final String UPDATE_ELO = "UPDATE stats SET elo = ? WHERE username = ?";
    private static final String GET_STATS_BY_USERNAME = "SELECT username, wins, losses, draws, games_played, elo FROM stats WHERE username = ?";
    private static final String INCREMENT_GAMES_PLAYED = "UPDATE stats SET games_played = games_played + 1 WHERE username = ?";
    private static final String ADD_DRAW = "UPDATE stats SET draws = draws + 1 WHERE username = ?";


    public StatsDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    @Override
    public void createStats(String username) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_STATS)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error inserting stats for user: " + username, e);
        }
    }
    @Override
    public void addWin(String username) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_WIN)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error adding win for user: " + username, e);
        }
    }

    @Override
    public void addLoss(String username) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_LOSS)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error adding loss for user: " + username, e);
        }
    }

    @Override
    public void updateElo(String username, int elo) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ELO)) {
            statement.setInt(1, elo);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating ELO for user: " + username, e);
        }
    }

    @Override
    public Object getStatsByUsername(String username) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_STATS_BY_USERNAME)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Object() {
                        public final String username = rs.getString("username");
                        public final int wins = rs.getInt("wins");
                        public final int losses = rs.getInt("losses");
                        public final int draws = rs.getInt("draws");
                        public final int gamesPlayed = rs.getInt("games_played");
                        public final int elo = rs.getInt("elo");
                    };
                }
                throw new RuntimeException("User stats not found for username: " + username);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving stats for user: " + username, e);
        }
    }

    @Override
    public void incrementGamesPlayed(String username) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(INCREMENT_GAMES_PLAYED)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating games played for " + username, e);
        }
    }
    @Override
    public void addDraw(String username) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(ADD_DRAW)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating draw for " + username, e);
        }
    }
}
