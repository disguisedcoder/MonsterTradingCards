package at.technikum.TradingCards.services;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.CardRepository;
import at.technikum.application.TradingCards.repository.DeckRepository;
import at.technikum.application.TradingCards.repository.StatsRepository;
import at.technikum.application.TradingCards.repository.UserRepository;
import at.technikum.application.TradingCards.service.BattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BattleServiceTest {

    private CardRepository cardRepository;
    private UserRepository userRepository;
    private StatsRepository statsRepository;
    private DeckRepository deckRepository;
    private BattleService battleService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        statsRepository = mock(StatsRepository.class);
        deckRepository = mock(DeckRepository.class);
        battleService = new BattleService(cardRepository, statsRepository, userRepository, deckRepository);
    }

    @Test
    void addPlayerToQueue_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByToken("invalidToken")).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> battleService.addPlayerToQueue("invalidToken"));
        assertEquals("Invalid user token.", exception.getMessage());
    }

    @Test
    void addPlayerToQueue_ShouldThrow_WhenUserHasNoDeck() {
        User user = new User("user", "pass");
        // Set an empty deck (List<Card>) for the user
        user.setDeck(new ArrayList<>());
        when(userRepository.findByToken("token")).thenReturn(user);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> battleService.addPlayerToQueue("token"));
        assertEquals("User has no deck.", exception.getMessage());
    }

    @Test
    void battleSimulation_ShouldConductBattleAndReturnResult() throws InterruptedException {
        // Create two users with non-empty decks (List<Card>)
        User user1 = new User("user1", "pass");
        User user2 = new User("user2", "pass");

        // Create dummy Card objects for the deck emptiness check in addPlayerToQueue.
        Card dummyDeckCard1 = mock(Card.class);
        Card dummyDeckCard2 = mock(Card.class);
        user1.setDeck(new ArrayList<>(List.of(dummyDeckCard1)));
        user2.setDeck(new ArrayList<>(List.of(dummyDeckCard2)));

        // Stub userRepository.findByToken to return the corresponding users.
        when(userRepository.findByToken("token1")).thenReturn(user1);
        when(userRepository.findByToken("token2")).thenReturn(user2);

        // Stub deckRepository.getDeck to return a list of card IDs.
        when(deckRepository.getDeck("user1")).thenReturn(List.of("card1"));
        when(deckRepository.getDeck("user2")).thenReturn(List.of("card2"));

        // Create dummy cards for the battle simulation and stub cardRepository.findById.
        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);
        when(cardRepository.findById("card1")).thenReturn(card1);
        when(cardRepository.findById("card2")).thenReturn(card2);

        // Stub card behavior: both cards can attack each other.
        when(card1.canAttack(card2)).thenReturn(true);
        when(card2.canAttack(card1)).thenReturn(true);
        // Set damage values so that card1 wins the round.
        when(card1.calculateDamageAgainst(card2)).thenReturn(10.0);
        when(card2.calculateDamageAgainst(card1)).thenReturn(5.0);

        // Simulate two players entering the battle queue concurrently.
        Thread thread1 = new Thread(() -> battleService.addPlayerToQueue("token1"));
        Thread thread2 = new Thread(() -> battleService.addPlayerToQueue("token2"));
        thread1.start();
        thread2.start();
        thread1.join(TimeUnit.SECONDS.toMillis(5));
        thread2.join(TimeUnit.SECONDS.toMillis(5));

        // Retrieve battle results.
        String result1 = battleService.getBattleResult("token1");
        String result2 = battleService.getBattleResult("token2");

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1, result2);
        assertTrue(result1.contains("wins the battle!"));

        // Verify that statsRepository methods were called as expected:
        // In our simulation, user1 should win because card1's damage > card2's damage.
        verify(statsRepository, times(1)).addWin("user1");
        verify(statsRepository, times(1)).addLoss("user2");
        verify(statsRepository, times(1)).updateElo(eq("user1"), anyInt());
        verify(statsRepository, times(1)).updateElo(eq("user2"), anyInt());
        verify(statsRepository, times(1)).incrementGamesPlayed("user1");
        verify(statsRepository, times(1)).incrementGamesPlayed("user2");

        verify(userRepository, times(1)).updateElo(eq("user1"), anyInt());
        verify(userRepository, times(1)).updateElo(eq("user2"), anyInt());
    }

    @Test
    void getBattleResult_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByToken("nonexistent")).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> battleService.getBattleResult("nonexistent"));
        assertEquals("Invalid user token.", exception.getMessage());
    }
}
