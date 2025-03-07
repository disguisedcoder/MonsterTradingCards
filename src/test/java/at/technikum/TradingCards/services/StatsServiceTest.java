package at.technikum.TradingCards.services;

import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.StatsRepository;
import at.technikum.application.TradingCards.repository.UserRepository;
import at.technikum.application.TradingCards.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private StatsRepository statsRepository;
    private UserRepository userRepository;
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        statsRepository = mock(StatsRepository.class);
        userRepository = mock(UserRepository.class);
        statsService = new StatsService(statsRepository, userRepository);
    }

    @Test
    void getStats_ShouldReturnStats_WhenTokenIsValid() {
        // Arrange
        String token = "validToken";
        User user = new User("testUser", "password");
        Object expectedStats = new Object();  // Replace with an appropriate stats object if needed

        when(userRepository.findByToken(token)).thenReturn(user);
        when(statsRepository.getStatsByUsername("testUser")).thenReturn(expectedStats);

        // Act
        Object stats = statsService.getStats(token);

        // Assert
        assertEquals(expectedStats, stats);
        verify(userRepository, times(1)).findByToken(token);
        verify(statsRepository, times(1)).getStatsByUsername("testUser");
    }

    @Test
    void getStats_ShouldThrowNullPointerException_WhenUserNotFound() {
        // Arrange
        String token = "invalidToken";
        when(userRepository.findByToken(token)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> statsService.getStats(token));
        verify(userRepository, times(1)).findByToken(token);
        verifyNoInteractions(statsRepository);
    }
}
