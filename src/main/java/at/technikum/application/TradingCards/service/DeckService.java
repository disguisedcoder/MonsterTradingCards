package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.DTO.CardDTO;
import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.repository.CardRepository;
import at.technikum.application.TradingCards.repository.DeckRepository;

import java.util.ArrayList;
import java.util.List;

public class DeckService {
    private final DeckRepository deckRepository;
    private final CardService cardService;
    private final CardRepository cardRepository;

    public DeckService(DeckRepository deckRepository, CardService cardService, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.cardService = cardService;
        this.cardRepository = cardRepository;
    }

    public void configureDeck(String token, List<String> cardIds) {
        String username = deckRepository.getUsernameFromToken(token);

        // Check if the user already has a deck
        if (deckRepository.hasDeck(username)) {
            deckRepository.clearDeck(username);
        }

        // Add cards to the deck
        deckRepository.addCardsToDeck(username, cardIds);
    }

    public List<CardDTO> getPlainDeckByToken(String token) {
        String username = deckRepository.getUsernameFromToken(token);

        // Hole die Karteninformationen direkt aus der DeckDbRepository
        List<String> plainDeck = deckRepository.getPlainDeck(username);

        // Konvertiere die Informationen in CardDTOs
        List<CardDTO> cardDTOs = new ArrayList<>();
        for (String cardDetails : plainDeck) {
            String[] parts = cardDetails.split(", ");
            String id = parts[0].split(": ")[1];
            String name = parts[1].split(": ")[1];
            double damage = Double.parseDouble(parts[2].split(": ")[1]);

            cardDTOs.add(new CardDTO(id, name, damage, username));
        }
        return cardDTOs;
    }

    public List<CardDTO> getDeckByToken(String token) {
        String username = deckRepository.getUsernameFromToken(token);

        List<String> cardIds = deckRepository.getDeck(username);

        // Erstelle CardDTOs basierend auf den IDs
        List<CardDTO> cardDTOs = new ArrayList<>();
        for (String cardId : cardIds) {
            Card card = cardRepository.findById(cardId); // Hole die vollständigen Karteninformationen
            if (card != null) {
                cardDTOs.add(new CardDTO(card.getId(), card.getName(), card.getDamage(), card.getUsername()));
            }
        }
        return cardDTOs;
    }
    public List<String> extractCardIds(List<CardDTO> cardDTOs) {
        return cardDTOs.stream()
                .map(CardDTO::getId) // Extract only the ID from each CardDTO
                .toList();
    }

}
