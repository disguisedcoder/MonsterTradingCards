package at.technikum.TradingCards.services;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.packages.Package;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.PackageRepository;
import at.technikum.application.TradingCards.repository.UserRepository;
import at.technikum.application.TradingCards.service.CardService;
import at.technikum.application.TradingCards.service.PackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackageServiceTest {

    private PackageRepository packageRepository;
    private UserRepository userRepository;
    private CardService cardService;
    private PackageService packageService;

    @BeforeEach
    void setUp() {
        packageRepository = mock(PackageRepository.class);
        userRepository = mock(UserRepository.class);
        cardService = mock(CardService.class);
        packageService = new PackageService(packageRepository, userRepository, cardService);
    }

    @Test
    void createPackage_ShouldThrow_WhenCardsAreNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> packageService.createPackage(null)
        );
        assertEquals("A package must contain exactly 5 cards.", ex.getMessage());
        verifyNoInteractions(packageRepository);
        verifyNoInteractions(cardService);
    }

    @Test
    void createPackage_ShouldThrow_WhenCardsSizeNotFive() {
        List<Card> fourCards = List.of(
                new Card("1", "Card1", 10),
                new Card("2", "Card2", 20),
                new Card("3", "Card3", 30),
                new Card("4", "Card4", 40)
        );
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> packageService.createPackage(fourCards)
        );
        assertEquals("A package must contain exactly 5 cards.", ex.getMessage());
        verifyNoInteractions(packageRepository);
        verifyNoInteractions(cardService);
    }

    @Test
    void createPackage_ShouldSavePackageAndCards_WhenCardsSizeIsFive() {
        List<Card> fiveCards = List.of(
                new Card("1", "Card1", 10),
                new Card("2", "Card2", 20),
                new Card("3", "Card3", 30),
                new Card("4", "Card4", 40),
                new Card("5", "Card5", 50)
        );
        when(packageRepository.save()).thenReturn(101);

        packageService.createPackage(fiveCards);

        verify(packageRepository, times(1)).save();
        // Verify that each card gets the package ID assigned
        for (Card card : fiveCards) {
            assertEquals(101, card.getPackage_id());
        }
        verify(cardService, times(1)).saveCards(fiveCards);
        verifyNoMoreInteractions(packageRepository);
        verifyNoMoreInteractions(cardService);
    }

    @Test
    void acquirePackage_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByToken("invalidToken")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> packageService.acquirePackage("invalidToken")
        );
        assertTrue(ex.getMessage().contains("Invalid user token."));
        verifyNoInteractions(packageRepository);
    }

    @Test
    void acquirePackage_ShouldThrow_WhenUserHasNotEnoughCoins() {
        User userWithFewCoins = new User("userA", "pass");
        userWithFewCoins.setCoins(4); // Less than the required 5 coins
        when(userRepository.findByToken("someToken")).thenReturn(userWithFewCoins);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> packageService.acquirePackage("someToken")
        );
        assertTrue(ex.getMessage().contains("Not enough money."));
        verifyNoInteractions(packageRepository);
    }

    @Test
    void acquirePackage_ShouldThrow_WhenNoPackagesAvailable() {
        User user = new User("userA", "pass");
        user.setCoins(10);
        when(userRepository.findByToken("validToken")).thenReturn(user);
        // Simulate no package available
        when(packageRepository.acquirePackage("userA")).thenReturn(null);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> packageService.acquirePackage("validToken")
        );
        assertTrue(ex.getMessage().contains("No packages available."));
        verify(userRepository, times(1)).findByToken("validToken");
        verify(packageRepository, times(1)).acquirePackage("userA");
        verifyNoMoreInteractions(packageRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void acquirePackage_ShouldAcquirePackageAndUpdateUser_WhenAllConditionsMet() {
        User user = new User("userA", "pass");
        user.setCoins(10);
        user.setStack(new ArrayList<>());

        List<Card> cardsInPackage = List.of(
                new Card("1", "Card1", 10),
                new Card("2", "Card2", 20)
        );
        Package pack = new Package(999, cardsInPackage);

        when(userRepository.findByToken("validToken")).thenReturn(user);
        when(packageRepository.acquirePackage("userA")).thenReturn(pack);

        packageService.acquirePackage("validToken");

        // Verify that coins have been deducted by 5
        assertEquals(5, user.getCoins());
        // Verify that the cards from the package have been added to the user's stack
        assertEquals(2, user.getStack().size());
        assertEquals("Card1", user.getStack().get(0).getName());

        verify(userRepository, times(1)).findByToken("validToken");
        verify(packageRepository, times(1)).acquirePackage("userA");
        verify(userRepository, times(1)).update(user);
        verifyNoMoreInteractions(packageRepository);
        verifyNoMoreInteractions(userRepository);
    }
}
