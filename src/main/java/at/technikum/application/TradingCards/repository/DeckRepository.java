package at.technikum.application.TradingCards.repository;

import java.util.List;

public interface DeckRepository {
    void clearDeck(String username);

    void addCardsToDeck(String username, List<String> cardIds);

    List<String> getDeck(String username);

    boolean hasDeck(String username);

    String getUsernameFromToken(String token);

    List<String> getPlainDeck(String token);

}
