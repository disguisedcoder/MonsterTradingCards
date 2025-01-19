package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.DTO.CardDTO;
import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.CardRepository;
import at.technikum.application.TradingCards.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public void saveCards(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            throw new IllegalArgumentException("Card list cannot be null or empty.");
        }
        for (Card card : cards) {
            cardRepository.save(card);
        }
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public List<Card> getCardsByUserToken(User user) {

        return cardRepository.findByUsername(user.getUsername());
    }

    public List<CardDTO> getDeckByToken(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid token.");
        }

        List<Card> cards = cardRepository.getDeck(user.getUsername());

        // Convert Cards to CardDTOs
        List<CardDTO> cardDTOs = new ArrayList<>();
        for (Card card : cards) {
            cardDTOs.add(new CardDTO(card.getId(), card.getName(), card.getDamage(), card.getUsername()));
        }
        return cardDTOs;

    }


    public List<CardDTO> getCardDTOsByUserToken(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid user token");
        }
        List<Card> cards = getCardsByUserToken(user);

        // Convert Cards to CardDTOs
        List<CardDTO> cardDTOs = new ArrayList<>();
        for (Card card : cards) {
            cardDTOs.add(new CardDTO(card.getId(), card.getName(), card.getDamage(), card.getUsername()));
        }
        return cardDTOs;
    }

    public List<CardDTO> getCardDTOsIdOnlyByUserToken(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid user token");
        }
        List<Card> cards = getCardsByUserToken(user);

        // Convert Cards to CardDTOs
        List<CardDTO> cardDTOs = new ArrayList<>();
        for (Card card : cards) {
            cardDTOs.add(new CardDTO(card.getId(), card.getName(), card.getDamage(), card.getUsername()));
        }
        return cardDTOs;
    }




}
