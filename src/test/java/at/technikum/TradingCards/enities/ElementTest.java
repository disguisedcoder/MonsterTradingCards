package at.technikum.application.TradingCards.enties;
import at.technikum.application.TradingCards.entity.card.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElementTest {

    @Test
    void isEffectiveAgainstTest() {
        assertTrue(Element.WATER.isEffectiveAgainst(Element.FIRE));
        assertFalse(Element.FIRE.isEffectiveAgainst(Element.WATER));
    }

    @Test
    void isNotEffectiveAgainstTest() {
        assertTrue(Element.FIRE.isNotEffectiveAgainst(Element.WATER));
        //assertFalse(Element.NORMAL.isNotEffectiveAgainst(Element.FIRE));
    }
}
