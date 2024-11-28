package at.technikum.application.TradingCards.entity.card;

public abstract class Card {
    private final String id;
    private final String name;
    private final int damage;
    private final Element element;

    public Card(String id, String name, int damage, Element element) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.element = element;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                ", element=" + element +
                '}';
    }
}
