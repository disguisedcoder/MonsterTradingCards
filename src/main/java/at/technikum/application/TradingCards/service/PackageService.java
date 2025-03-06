package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.packages.Package;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.PackageRepository;
import at.technikum.application.TradingCards.repository.UserRepository;

import java.util.List;

public class PackageService {
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final CardService cardService;


    public PackageService(PackageRepository packageRepository, UserRepository userRepository, CardService cardService) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
        this.cardService = cardService;
    }

    public void createPackage(List<Card> cards) {
        if (cards == null || cards.size() != 5) {
            throw new IllegalArgumentException("A package must contain exactly 5 cards.");
        }
        int packageId = packageRepository.save();
        // Assign the package ID to the cards
        cards.forEach(card -> card.setPackage_id(packageId));

        // Save the cards
        cardService.saveCards(cards);


    }

    public void acquirePackage(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid user token.");
        }
        if (user.getCoins() < 5) {
            throw new IllegalStateException("Not enough money.");
        }

        Package pack = packageRepository.acquirePackage(user.getUsername());
        if (pack == null) {
            throw new IllegalStateException("No packages available.");
        }

        user.setCoins(user.getCoins() - 5);
        List<Card> updatedStack = user.getStack();
        updatedStack.addAll(pack.getCards());
        user.setStack(updatedStack);
        userRepository.update(user);// Stack aktualisieren

    }

}
