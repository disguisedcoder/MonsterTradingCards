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

    // ----------------------------------------------------------------
    // createPackage(List<Card>)
    // ----------------------------------------------------------------

    @Test
    void createPackage_ShouldThrow_WhenCardsAreNull() {
        // Act & Assert
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
        // Arrange
        List<Card> fourCards = List.of(
                new Card("1", "Card1", 10),
                new Card("2", "Card2", 20),
                new Card("3", "Card3", 30),
                new Card("4", "Card4", 40)
        );

        // Act & Assert
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
        // Arrange
        List<Card> fiveCards = List.of(
                new Card("1", "Card1", 10),
                new Card("2", "Card2", 20),
                new Card("3", "Card3", 30),
                new Card("4", "Card4", 40),
                new Card("5", "Card5", 50)
        );

        when(packageRepository.save()).thenReturn(101);

        // Act
        packageService.createPackage(fiveCards);

        // Assert
        verify(packageRepository, times(1)).save();
        // Each card should get package_id=101
        for (Card card : fiveCards) {
            assertEquals(101, card.getPackage_id());
        }
        verify(cardService, times(1)).saveCards(fiveCards);
        verifyNoMoreInteractions(packageRepository);
        verifyNoMoreInteractions(cardService);
    }

    @Test
    void acquirePackage_ShouldThrow_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByToken("invalidToken")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> packageService.acquirePackage("invalidToken")
        );
        assertTrue(ex.getMessage().contains("Invalid user token."));
        verifyNoInteractions(packageRepository);
    }

    @Test
    void acquirePackage_ShouldThrow_WhenUserHasNotEnoughCoins() {
        // Arrange
        User userWithFewCoins = new User("userA", "pass");
        userWithFewCoins.setCoins(4); // less than 5
        when(userRepository.findByToken("someToken")).thenReturn(userWithFewCoins);

        // Act & Assert
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> packageService.acquirePackage("someToken")
        );
        assertTrue(ex.getMessage().contains("Not enough money"));
        verifyNoInteractions(packageRepository);
    }

    @Test
    void acquirePackage_ShouldThrow_WhenNoPackagesAvailable() {
        // Arrange
        User user = new User("userA", "pass");
        user.setCoins(10);
        when(userRepository.findByToken("validToken")).thenReturn(user);

        // No package returned means none available
        when(packageRepository.acquirePackage("userA")).thenReturn(null);

        // Act & Assert
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
        // Arrange
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

        // Act
        packageService.acquirePackage("validToken");

        // Assert
        // 1) User coins decreased by 5
        assertEquals(5, user.getCoins());
        // 2) Cards added to user stack
        assertEquals(2, user.getStack().size());
        assertEquals("Card1", user.getStack().get(0).getName());

        // Verify calls
        verify(userRepository, times(1)).findByToken("validToken");
        verify(packageRepository, times(1)).acquirePackage("userA");
        verify(userRepository, times(1)).update(user);

        // No other calls on these mocks
        verifyNoMoreInteractions(packageRepository);
        verifyNoMoreInteractions(userRepository);
    }
}