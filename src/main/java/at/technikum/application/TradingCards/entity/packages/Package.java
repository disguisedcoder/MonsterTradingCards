package at.technikum.application.TradingCards.entity.packages;

import at.technikum.application.TradingCards.entity.card.Card;

import java.util.List;

public class Package {

    private final String id;
    private final List<Card> cards; // Enthält die Karten des Pakets (genau 5)

    public Package(String id, List<Card> cards) {
        if (cards == null || cards.size() != 5) {
            throw new IllegalArgumentException("A package must contain exactly 5 cards.");
        }
        this.id = id;
        this.cards = cards;
    }

    public String getId() {
        return id;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return "Package{" +
                "id='" + id + '\'' +
                ", cards=" + cards +
                '}';
    }
}
