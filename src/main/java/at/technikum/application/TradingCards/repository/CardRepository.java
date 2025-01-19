package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.entity.card.Card;

import java.util.List;

public interface CardRepository {
    void save(Card card);

    Card findById(String cardId);
    List<Card> findByUsername(String username);
    List<Card> findAll();
}
