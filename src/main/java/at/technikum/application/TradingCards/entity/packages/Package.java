package at.technikum.application.TradingCards.entity.packages;

import at.technikum.application.TradingCards.entity.card.Card;

import java.util.List;

public class Package {
    private int id;
    private List<Card> cards;

    public Package(int id, List<Card> cards) {
        this.id = id;
        this.cards = cards;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public String toString() {
        return "Package{" +
                "id=" + id +
                ", cards=" + cards +
                '}';
    }
}