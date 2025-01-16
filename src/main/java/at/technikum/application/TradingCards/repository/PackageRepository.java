package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.packages.Package;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PackageRepository {
    private final Queue<Package> packages = new LinkedList<>();
    private int nextId = 1; // Startwert für die ID

    public void save(List<Card> cards) {
        Package pack = new Package(nextId++, cards); // Vergibt die nächste ID und erhöht sie
        packages.add(pack);
    }

    public Package acquirePackage() {
        return packages.poll(); // Gibt das erste Paket zurück und entfernt es
    }

    public boolean hasPackages() {
        return !packages.isEmpty();
    }
}
