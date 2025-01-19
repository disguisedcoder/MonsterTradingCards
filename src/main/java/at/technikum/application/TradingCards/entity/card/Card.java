package at.technikum.application.TradingCards.entity.card;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Card {
    private final String id;
    private final String name;
    private final double damage;
    private final Element element;
    private final boolean isMonster; // true, wenn es eine Monsterkarte ist
    private final boolean isSpell;   // true, wenn es eine Zauberkarte ist
    private final MonsterType monsterType; // Nur für Monsterkarten relevant
    private int package_id;
    private String username;


    // Constructor for Jackson deserialization
    @JsonCreator
    public Card(
            @JsonProperty("ID") String id,
            @JsonProperty("name") String name,
            @JsonProperty("damage") double damage
    ) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.element = determineElement(name);
        this.isSpell = name.contains("Spell");
        this.isMonster = !this.isSpell;
        this.monsterType = determineMonsterType(name);
    }
    private Element determineElement(String name) {
        if (name.toLowerCase().contains("water")) {
            return Element.WATER;
        } else if (name.toLowerCase().contains("fire")) {
            return Element.FIRE;
        } else {
            return Element.NORMAL;
        }
    }

    public int getPackage_id(){
        return package_id;
    }

    public void setPackage_id(int packageId) {
        this.package_id = packageId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private MonsterType determineMonsterType(String name) {
        if (name.toLowerCase().contains("goblin")) {
            return MonsterType.GOBLIN;
        } else if (name.toLowerCase().contains("dragon")) {
            return MonsterType.DRAGON;
        } else if (name.toLowerCase().contains("ork")) {
            return MonsterType.ORK;
        } else if (name.toLowerCase().contains("wizard")) {
            return MonsterType.WIZARD;
        } else if (name.toLowerCase().contains("knight")) {
            return MonsterType.KNIGHT;
        } else if (name.toLowerCase().contains("kraken")) {
            return MonsterType.KRAKEN;
        } else if (name.toLowerCase().contains("elf")) {
            return MonsterType.ELVE;
        } else {
            return null; // Standardwert
        }
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


