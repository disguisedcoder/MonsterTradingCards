package at.technikum.application.TradingCards.entity.card;


public class Card {
    private final String id;
    private final String name;
    private final double damage;
    private final Element element;
    private final boolean isMonster; // true, wenn es eine Monsterkarte ist
    private final boolean isSpell;   // true, wenn es eine Zauberkarte ist
    private final MonsterType monsterType; // Nur für Monsterkarten relevant

    // Konstruktor für Monsterkarten
    public Card(String id, String name, double damage, Element element, MonsterType monsterType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.element = element;
        this.isMonster = true;
        this.isSpell = false;
        this.monsterType = monsterType;
    }

    // Konstruktor für Zauberkarten
    public Card(String id, String name, double damage, Element element) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.element = element;
        this.isMonster = false;
        this.isSpell = true;
        this.monsterType = null; // Keine MonsterType für Zauberkarten
    }

    // Berechnung des Schadens mit Element-Effektivität
    public double calculateDamageAgainst(Card opponent) {
        if (this.isMonster && opponent.isMonster) {
            // Reiner Monsterkampf: Kein Element-Effekt
            return this.damage;
        }

        if (this.isSpell || opponent.isSpell) {
            // Effektivitätslogik bei Spell-Karten
            if (this.element.isEffectiveAgainst(opponent.element)) {
                return this.damage * 2;
            } else if (this.element.isNotEffectiveAgainst(opponent.element)) {
                return this.damage / 2;
            }
        }

        // Standard: Kein Effekt
        return this.damage;
    }

    // Spezialfähigkeiten
    public boolean canAttack(Card opponent) {
        if (this.isMonster && this.monsterType == MonsterType.GOBLIN && opponent.monsterType == MonsterType.DRAGON) {
            return false; // Goblins greifen keine Drachen an
        }
        if (this.isMonster && this.monsterType == MonsterType.WIZARD && opponent.monsterType == MonsterType.ORK) {
            return false; // Zauberer kontrollieren Orks
        }
        if (this.isMonster && this.monsterType == MonsterType.KNIGHT && opponent.isSpell && opponent.element == Element.WATER) {
            return false; // Ritter ertrinken bei Wassersprüchen
        }
        if (this.isMonster && this.monsterType == MonsterType.KRAKEN && opponent.isSpell) {
            return false; // Kraken sind immun gegen Zauber
        }
        if (this.isMonster && this.monsterType == MonsterType.ELVE && this.element == Element.FIRE && opponent.monsterType == MonsterType.DRAGON) {
            return false; // Feuerelfen weichen Drachen aus
        }
        return true; // Standard: Kann angreifen
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public Element getElement() {
        return element;
    }

    public boolean isMonster() {
        return isMonster;
    }

    public boolean isSpell() {
        return isSpell;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                ", element=" + element +
                ", isMonster=" + isMonster +
                ", isSpell=" + isSpell +
                ", monsterType=" + monsterType +
                '}';
    }
}


