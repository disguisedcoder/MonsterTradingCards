package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.packages.Package;
import at.technikum.application.TradingCards.repository.PackageRepository;
import at.technikum.application.TradingCards.repository.UserDbRepository;

import java.util.ArrayList;
import java.util.List;

public class PackageService {
    private final PackageRepository packageRepository;
    private final UserDbRepository userRepository;

    public PackageService(PackageRepository packageRepository, UserDbRepository userRepository) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
    }

    public void createPackage(List<Card> cards) {
        if (cards == null || cards.size() != 5) {
            throw new IllegalArgumentException("A package must contain exactly 5 cards.");
        }
        packageRepository.save(cards); // Speichert das Paket und weist eine ID zu
    }

    public void acquirePackage(String token) {
        var user = userRepository.findByToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid user token.");
        }
        if (user.getCoins() < 5) {
            throw new IllegalStateException("Not enough coins to acquire a package.");
        }

        Package pack = packageRepository.acquirePackage();
        if (pack == null) {
            throw new IllegalStateException("No packages available.");
        }

        user.setCoins(user.getCoins() - 5);
        List<Card> updatedStack = new ArrayList<>(user.getStack());
        updatedStack.addAll(pack.getCards());
        user.setStack(updatedStack); // Stack aktualisieren    }
    }
}
