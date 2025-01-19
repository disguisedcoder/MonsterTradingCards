package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.StatsRepository;
import at.technikum.application.TradingCards.repository.UserRepository;

import java.util.List;

public class StatsService {

    private final StatsRepository statsRepository;
    private final UserRepository userRepository;

    public StatsService(StatsRepository statsRepository, UserRepository userRepository) {
        this.statsRepository = statsRepository;
        this.userRepository = userRepository;

    }

    public Object getStats(String token) {
        User user = userRepository.findByToken(token);
        return statsRepository.getStatsByUsername(user.getUsername());
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
