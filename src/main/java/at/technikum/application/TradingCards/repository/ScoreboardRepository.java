package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.DTO.ScoreboardEntry;
import java.util.List;

public interface ScoreboardRepository {
    List<ScoreboardEntry> getScoreboard();
}
