package at.technikum.application.TradingCards.entity.card;

public class SpellCard extends Card {

    public SpellCard(String id, String name, int damage, Element element) {
        super(id, name, damage, element);
    }

    @Override
    public String toString() {
        return "SpellCard{" +
                "element=" + getElement() +
                ", name='" + getName() + '\'' +
                ", damage=" + getDamage() +
                '}';
    }

}
