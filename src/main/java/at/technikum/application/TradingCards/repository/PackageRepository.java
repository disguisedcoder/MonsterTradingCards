package at.technikum.application.TradingCards.repository;

import at.technikum.application.TradingCards.entity.packages.Package;

public interface PackageRepository {

    /**
     * Saves a new package to the database and returns its ID.
     *
     * @return The ID of the saved package.
     */
    int save();

    /**
     * Retrieves a random available package from the database and marks it as acquired.
     *
     * @param username The username of the buyer.
     * @return The acquired package or null if no package is available.
     */
    Package acquirePackage(String username);

    /**
     * Checks if there are available packages.
     *
     * @return true if packages are available, otherwise false.
     */
    boolean hasPackages();
}
