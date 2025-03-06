package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.DTO.ScoreboardEntry;
import at.technikum.application.data.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardDbRepository implements ScoreboardRepository {

    private final ConnectionPool connectionPool;
    private static final String GET_SCOREBOARD = "SELECT username, elo FROM stats ORDER BY elo DESC";

    public ScoreboardDbRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<ScoreboardEntry> getScoreboard() {
        List<ScoreboardEntry> scoreboard = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_SCOREBOARD);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("username");
                int elo = rs.getInt("elo");
                scoreboard.add(new ScoreboardEntry(username, elo));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving scoreboard.", e);
        }
        return scoreboard;
    }
}
