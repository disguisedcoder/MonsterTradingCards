package at.technikum.application.TradingCards.entity.card;

public enum Element {
    WATER,
    FIRE,
    NORMAL;

    public boolean isEffectiveAgainst(Element other) {
        return (this == FIRE && other == NORMAL) ||
                (this == WATER && other == FIRE) ||
                (this == NORMAL && other == WATER);
    }

    public boolean isNotEffectiveAgainst(Element other) {
        return (this == FIRE && other == WATER) ||
                (this == WATER && other == NORMAL) ||
                (this == NORMAL && other == FIRE);
    }
}
