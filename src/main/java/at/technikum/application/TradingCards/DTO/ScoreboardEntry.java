package at.technikum.application.TradingCards.DTO;

public class ScoreboardEntry {
    private String username;
    private int elo;

    public ScoreboardEntry(String username, int elo) {
        this.username = username;
        this.elo = elo;
    }

    public String getUsername() {
        return username;
    }

    public int getElo() {
        return elo;
    }

    @Override
    public String toString() {
        return "Scoreboard{" +
                "username='" + username + '\'' +
                ", elo=" + elo +
                '}';
    }
}