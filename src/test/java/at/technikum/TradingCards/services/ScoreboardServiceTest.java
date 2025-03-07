package at.technikum.TradingCards.services;

import at.technikum.application.TradingCards.DTO.ScoreboardEntry;
import at.technikum.application.TradingCards.repository.ScoreboardRepository;
import at.technikum.application.TradingCards.service.ScoreboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreboardServiceTest {

    private ScoreboardRepository scoreboardRepository;
    private ScoreboardService scoreboardService;

    @BeforeEach
    void setUp() {
        scoreboardRepository = mock(ScoreboardRepository.class);
        scoreboardService = new ScoreboardService(scoreboardRepository);
    }

    @Test
    void getScoreboard_ShouldReturnScoreboardEntries() {
        // Arrange
        List<ScoreboardEntry> mockScoreboard = List.of(
                new ScoreboardEntry("user1", 100),
                new ScoreboardEntry("user2", 80)
        );
        when(scoreboardRepository.getScoreboard()).thenReturn(mockScoreboard);

        // Act
        List<ScoreboardEntry> result = scoreboardService.getScoreboard();

        // Assert
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals(100, result.get(0).getElo());
        verify(scoreboardRepository, times(1)).getScoreboard();
    }

    @Test
    void getScoreboard_ShouldReturnEmptyList_WhenNoEntries() {
        // Arrange
        when(scoreboardRepository.getScoreboard()).thenReturn(List.of());

        // Act
        List<ScoreboardEntry> result = scoreboardService.getScoreboard();

        // Assert
        assertTrue(result.isEmpty());
        verify(scoreboardRepository, times(1)).getScoreboard();
    }
}
