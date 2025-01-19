package at.technikum.application.TradingCards.repository;

import at.technikum.application.data.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StatsDbRepository implements StatsRepository {

    private final ConnectionPool connectionPool;

    private static final String ADD_WIN = "UPDATE stats SET wins = wins + 1 WHERE username = ?";
    private static final String ADD_LOSS = "UPDATE stats SET losses = losses + 1 WHERE username = ?";
    private static final String UPDATE_ELO = "UPDATE stats SET elo = ? WHERE username = ?";
    private static final String GET_STATS_BY_USERNAME = "SELECT username, wins, losses, elo FROM stats WHERE username = ?";
    private static final String GET_SCOREBOARD = "SELECT username, wins, losses, elo FROM stats ORDER BY wins DESC, losses ASC";

    public StatsDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
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
    public List<Object> getScoreboard() {
        List<Object> scoreboard = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_SCOREBOARD);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                scoreboard.add(new Object() {
                    public final String username = rs.getString("username");
                    public final int wins = rs.getInt("wins");
                    public final int losses = rs.getInt("losses");
                    public final int elo = rs.getInt("elo");
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving scoreboard.", e);
        }
        return scoreboard;
    }
}
