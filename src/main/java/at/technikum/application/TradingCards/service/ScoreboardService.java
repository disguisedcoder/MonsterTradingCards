package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.DTO.ScoreboardEntry;
import at.technikum.application.TradingCards.repository.ScoreboardRepository;
import java.util.List;

public class ScoreboardService {

    private final ScoreboardRepository scoreboardRepository;

    public ScoreboardService(ScoreboardRepository scoreboardRepository) {
        this.scoreboardRepository = scoreboardRepository;
    }

    public List<ScoreboardEntry> getScoreboard() {
        return scoreboardRepository.getScoreboard();
    }
}
