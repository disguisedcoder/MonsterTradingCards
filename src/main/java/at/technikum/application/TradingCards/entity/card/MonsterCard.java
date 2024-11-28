package at.technikum.application.TradingCards.entity.card;

public class MonsterCard extends Card {
    private final MonsterType monsterType;

    public MonsterCard(String id, String name, int damage, Element element, MonsterType monsterType) {
        super(id, name, damage, element);
        this.monsterType= monsterType;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    @Override
    public String toString() {
        return "MonsterCard{" +
                "monsterType=" + monsterType +
                ", element=" + getElement() +
                ", name='" + getName() + '\'' +
                ", damage=" + getDamage() +
                '}';
    }
}
