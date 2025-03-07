package at.technikum.TradingCards.services;

import at.technikum.application.TradingCards.DTO.CardDTO;
import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.repository.CardRepository;
import at.technikum.application.TradingCards.repository.DeckRepository;
import at.technikum.application.TradingCards.service.CardService;
import at.technikum.application.TradingCards.service.DeckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeckServiceTest {

    private DeckRepository deckRepository;
    private CardService cardService;
    private CardRepository cardRepository;
    private DeckService deckService;

    @BeforeEach
    void setUp() {
        deckRepository = mock(DeckRepository.class);
        cardService = mock(CardService.class); // Not used directly but required by the constructor
        cardRepository = mock(CardRepository.class);
        deckService = new DeckService(deckRepository, cardService, cardRepository);
    }

    @Test
    void configureDeck_ShouldClearAndAddCards_WhenUserAlreadyHasDeck() {
        String token = "validToken";
        String username = "testUser";
        List<String> cardIds = Arrays.asList("card1", "card2");

        when(deckRepository.getUsernameFromToken(token)).thenReturn(username);
        when(deckRepository.hasDeck(username)).thenReturn(true);

        deckService.configureDeck(token, cardIds);

        verify(deckRepository, times(1)).clearDeck(username);
        verify(deckRepository, times(1)).addCardsToDeck(username, cardIds);
    }

    @Test
    void configureDeck_ShouldAddCardsWithoutClearing_WhenUserHasNoDeck() {
        String token = "validToken";
        String username = "testUser";
        List<String> cardIds = Arrays.asList("card1", "card2");

        when(deckRepository.getUsernameFromToken(token)).thenReturn(username);
        when(deckRepository.hasDeck(username)).thenReturn(false);

        deckService.configureDeck(token, cardIds);

        verify(deckRepository, never()).clearDeck(username);
        verify(deckRepository, times(1)).addCardsToDeck(username, cardIds);
    }

    @Test
    void getPlainDeckByToken_ShouldReturnCardDTOs_WhenPlainDeckDataIsValid() {
        String token = "validToken";
        String username = "testUser";
        // Example plain deck strings in the expected format:
        List<String> plainDeck = Arrays.asList(
                "id: card1, name: FireDragon, damage: 50.0",
                "id: card2, name: WaterSpirit, damage: 30.5"
        );

        when(deckRepository.getUsernameFromToken(token)).thenReturn(username);
        when(deckRepository.getPlainDeck(username)).thenReturn(plainDeck);

        List<CardDTO> result = deckService.getPlainDeckByToken(token);

        assertEquals(2, result.size());

        CardDTO firstCard = result.get(0);
        assertEquals("card1", firstCard.getId());
        assertEquals("FireDragon", firstCard.getName());
        assertEquals(50.0, firstCard.getDamage());
        assertEquals(username, firstCard.getUsername());

        CardDTO secondCard = result.get(1);
        assertEquals("card2", secondCard.getId());
        assertEquals("WaterSpirit", secondCard.getName());
        assertEquals(30.5, secondCard.getDamage());
        assertEquals(username, secondCard.getUsername());
    }

    @Test
    void getPlainDeckByToken_ShouldReturnEmptyList_WhenPlainDeckIsEmpty() {
        String token = "validToken";
        String username = "testUser";

        when(deckRepository.getUsernameFromToken(token)).thenReturn(username);
        when(deckRepository.getPlainDeck(username)).thenReturn(List.of());

        List<CardDTO> result = deckService.getPlainDeckByToken(token);

        assertTrue(result.isEmpty());
    }

    @Test
    void getDeckByToken_ShouldReturnCardDTOs_WhenCardsAreFound() {
        String token = "validToken";
        String username = "testUser";
        List<String> cardIds = Arrays.asList("card1", "card2");

        when(deckRepository.getUsernameFromToken(token)).thenReturn(username);
        when(deckRepository.getDeck(username)).thenReturn(cardIds);

        // Create card instances that will be returned by the CardRepository
        Card card1 = new Card("card1", "FireDragon", 50.0);
        card1.setUsername(username);
        Card card2 = new Card("card2", "WaterSpirit", 30.5);
        card2.setUsername(username);

        when(cardRepository.findById("card1")).thenReturn(card1);
        when(cardRepository.findById("card2")).thenReturn(card2);

        List<CardDTO> result = deckService.getDeckByToken(token);

        assertEquals(2, result.size());

        CardDTO firstCard = result.get(0);
        assertEquals("card1", firstCard.getId());
        assertEquals("FireDragon", firstCard.getName());
        assertEquals(50.0, firstCard.getDamage());
        assertEquals(username, firstCard.getUsername());

        CardDTO secondCard = result.get(1);
        assertEquals("card2", secondCard.getId());
        assertEquals("WaterSpirit", secondCard.getName());
        assertEquals(30.5, secondCard.getDamage());
        assertEquals(username, secondCard.getUsername());
    }

    @Test
    void getDeckByToken_ShouldSkipNullCards_WhenCardRepositoryReturnsNull() {
        String token = "validToken";
        String username = "testUser";
        List<String> cardIds = Arrays.asList("card1", "card2");

        when(deckRepository.getUsernameFromToken(token)).thenReturn(username);
        when(deckRepository.getDeck(username)).thenReturn(cardIds);

        // Only one card is found; the other returns null.
        Card card1 = new Card("card1", "FireDragon", 50.0);
        card1.setUsername(username);
        when(cardRepository.findById("card1")).thenReturn(card1);
        when(cardRepository.findById("card2")).thenReturn(null);

        List<CardDTO> result = deckService.getDeckByToken(token);

        assertEquals(1, result.size());
        CardDTO firstCard = result.get(0);
        assertEquals("card1", firstCard.getId());
        assertEquals("FireDragon", firstCard.getName());
        assertEquals(50.0, firstCard.getDamage());
        assertEquals(username, firstCard.getUsername());
    }

    @Test
    void extractCardIds_ShouldReturnListOfIds_WhenCardDTOsProvided() {
        List<CardDTO> cardDTOs = Arrays.asList(
                new CardDTO("card1", "FireDragon", 50.0, "testUser"),
                new CardDTO("card2", "WaterSpirit", 30.5, "testUser")
        );

        List<String> ids = deckService.extractCardIds(cardDTOs);

        assertEquals(2, ids.size());
        assertTrue(ids.contains("card1"));
        assertTrue(ids.contains("card2"));
    }

    @Test
    void extractCardIds_ShouldReturnEmptyList_WhenEmptyListProvided() {
        List<String> ids = deckService.extractCardIds(List.of());
        assertTrue(ids.isEmpty());
    }
}
