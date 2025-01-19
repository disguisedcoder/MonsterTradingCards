package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.repository.StatsRepository;

import java.util.List;

public class StatsService {

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public Object getStats(String username) {
        return statsRepository.getStatsByUsername(username);
    }

    public List<Object> getScoreboard() {
        return statsRepository.getScoreboard();
    }

    public void addWin(String username) {
        statsRepository.addWin(username);
    }

    public void addLoss(String username) {
        statsRepository.addLoss(username);
    }

    public void updateElo(String username, int elo) {
        statsRepository.updateElo(username, elo);
    }
}
