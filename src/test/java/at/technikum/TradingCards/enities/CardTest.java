package at.technikum.TradingCards.enities;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.card.Element;
import at.technikum.application.TradingCards.entity.card.MonsterType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void cardInitializationTest() {
        Card card = new Card("1", "FireDragon", 50);

        Assertions.assertEquals("1", card.getId());
        Assertions.assertEquals("FireDragon", card.getName());
        Assertions.assertEquals(50, card.getDamage());
        Assertions.assertEquals(Element.FIRE, card.getElement());
        Assertions.assertTrue(card.isMonster());
        Assertions.assertFalse(card.isSpell());
        Assertions.assertEquals(MonsterType.DRAGON, card.getMonsterType());
    }

    @Test
    void calculateDamageAgainstTest() {
        Card fireCard = new Card("1", "FireSpell", 40);
        Card waterCard = new Card("2", "WaterSpell", 30);

        double damage = fireCard.calculateDamageAgainst(waterCard);
        Assertions.assertEquals(20, damage, 0.1); // Fire is not effective against Water
    }

    @Test
    void canAttackSpecialRulesTest() {
        Card goblin = new Card("3", "Goblin", 20);
        Card dragon = new Card("4", "Dragon", 50);

        Assertions.assertFalse(goblin.canAttack(dragon)); // Goblins cannot attack Dragons
    }
}
