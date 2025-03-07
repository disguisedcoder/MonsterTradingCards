package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.StatsRepository;
import at.technikum.application.TradingCards.repository.UserRepository;

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
    
}
