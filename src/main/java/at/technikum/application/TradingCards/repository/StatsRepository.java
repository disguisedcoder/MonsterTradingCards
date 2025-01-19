package at.technikum.application.TradingCards.repository;

import java.util.List;

public interface StatsRepository {

    void addWin(String username);

    void addLoss(String username);

    void updateElo(String username, int elo);

    Object getStatsByUsername(String username);

    List<Object> getScoreboard();
}
