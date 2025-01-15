package at.technikum.application.TradingCards.repository;

import java.util.ArrayList;
import java.util.List;

public class PackageRepository {

    private final List<Package> packages = new ArrayList<>();

    public void save(Package pack) {
        packages.add(pack);
    }

    public List<Package> getAllPackages() {
        return new ArrayList<>(packages); // Gibt eine Kopie der Liste zurück
    }

    public boolean hasPackages() {
        return !packages.isEmpty();
    }

    public Package acquirePackage() {
        if (packages.isEmpty()) {
            throw new IllegalStateException("No packages available.");
        }
        return packages.remove(0); // Gibt das erste Paket zurück und entfernt es
    }
}
