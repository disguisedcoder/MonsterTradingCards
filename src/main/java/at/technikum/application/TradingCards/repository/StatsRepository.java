package at.technikum.application.TradingCards.repository;

import java.util.List;

public interface StatsRepository {

    void createStats(String username);

    void addWin(String username);

    void addLoss(String username);

    void updateElo(String username, int elo);

    Object getStatsByUsername(String username);

    void addDraw(String username);

    void incrementGamesPlayed(String username);


}
