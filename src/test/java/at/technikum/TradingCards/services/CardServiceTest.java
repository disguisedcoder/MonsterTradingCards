package at.technikum.TradingCards.services;

import at.technikum.application.TradingCards.DTO.CardDTO;
import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.CardRepository;
import at.technikum.application.TradingCards.repository.UserRepository;
import at.technikum.application.TradingCards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    private CardRepository cardRepository;
    private UserRepository userRepository;
    private CardService cardService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        cardService = new CardService(cardRepository, userRepository);
    }

    @Test
    void saveCards_ShouldSaveCards_WhenCardListIsValid() {
        List<Card> cards = Arrays.asList(
                new Card("1", "WaterGoblin", 10),
                new Card("2", "FireSpell", 20)
        );

        cardService.saveCards(cards);

        verify(cardRepository, times(1)).save(cards.get(0));
        verify(cardRepository, times(1)).save(cards.get(1));
    }

    @Test
    void saveCards_ShouldThrowException_WhenCardListIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.saveCards(null));

        assertEquals("Card list cannot be null or empty.", exception.getMessage());
        verifyNoInteractions(cardRepository);
    }

    @Test
    void saveCards_ShouldThrowException_WhenCardListIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.saveCards(List.of()));

        assertEquals("Card list cannot be null or empty.", exception.getMessage());
        verifyNoInteractions(cardRepository);
    }

    @Test
    void getAllCards_ShouldReturnAllCards() {
        List<Card> mockCards = Arrays.asList(
                new Card("1", "WaterGoblin", 10),
                new Card("2", "FireSpell", 20)
        );
        when(cardRepository.findAll()).thenReturn(mockCards);

        List<Card> result = cardService.getAllCards();

        assertEquals(2, result.size());
        assertEquals("WaterGoblin", result.get(0).getName());
        verify(cardRepository, times(1)).findAll();
    }

    @Test
    void getDeckByToken_ShouldReturnDeck_WhenTokenIsValid() {
        User mockUser = new User("testUser", "password");
        when(userRepository.findByToken("validToken")).thenReturn(mockUser);

        List<Card> mockDeck = Arrays.asList(
                new Card("1", "WaterGoblin", 10),
                new Card("2", "FireSpell", 20)
        );
        when(cardRepository.getDeck(mockUser.getUsername())).thenReturn(mockDeck);

        List<CardDTO> result = cardService.getDeckByToken("validToken");

        assertEquals(2, result.size());
        assertEquals("WaterGoblin", result.get(0).getName());
        verify(cardRepository, times(1)).getDeck(mockUser.getUsername());
    }

    @Test
    void getDeckByToken_ShouldThrowException_WhenTokenIsInvalid() {
        when(userRepository.findByToken("invalidToken")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.getDeckByToken("invalidToken"));

        assertEquals("Invalid token.", exception.getMessage());
        verifyNoInteractions(cardRepository);
    }

    @Test
    void getCardDTOsByUserToken_ShouldReturnCards_WhenTokenIsValid() {
        User mockUser = new User("testUser", "password");
        when(userRepository.findByToken("validToken")).thenReturn(mockUser);

        List<Card> mockCards = Arrays.asList(
                new Card("1", "WaterGoblin", 10),
                new Card("2", "FireSpell", 20)
        );
        when(cardRepository.findByUsername(mockUser.getUsername())).thenReturn(mockCards);

        List<CardDTO> result = cardService.getCardDTOsByUserToken("validToken");

        assertEquals(2, result.size());
        assertEquals("WaterGoblin", result.get(0).getName());
        verify(cardRepository, times(1)).findByUsername(mockUser.getUsername());
    }

    @Test
    void getCardDTOsByUserToken_ShouldThrowException_WhenTokenIsInvalid() {
        when(userRepository.findByToken("invalidToken")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.getCardDTOsByUserToken("invalidToken"));

        assertEquals("Invalid user token", exception.getMessage());
        verifyNoInteractions(cardRepository);
    }
}
