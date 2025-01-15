package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.repository.PackageRepository;
import at.technikum.application.TradingCards.entity.packages.Package;

import java.util.List;
import java.util.UUID;

public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public void createPackage(List<Card> cards) {
        String packageId = UUID.randomUUID().toString(); // Generiert eine eindeutige ID
        Package pack = new Package(packageId, cards);
        packageRepository.save(pack);
    }

    public List<Package> getAllPackages() {
        return packageRepository.getAllPackages();
    }
}
