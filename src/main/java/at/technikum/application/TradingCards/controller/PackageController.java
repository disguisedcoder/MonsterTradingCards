package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.service.PackageService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class PackageController extends Controller {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod().name(); // Get the HTTP method as a string

        if ("/packages".equals(path) && "POST".equalsIgnoreCase(method)) {
            return handleCreatePackage(request);
        } else if ("/transactions/packages".equals(path) && "POST".equalsIgnoreCase(method)) {
            return handleAcquirePackage(request);
        } else {
            return json(Status.NOT_FOUND, "Endpoint not found");
        }
    }

    private Response handleCreatePackage(Request request) {
        try {
            // Validate Authorization header
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.equals("Bearer admin-mtcgToken")) {
                return json(Status.UNAUTHORIZED, "Unauthorized access");
            }

            // Parse JSON body to a list of Card objects
            List<Card> cards = fromBody(request.getBody(), List.class);

            // Delegate package creation to the service
            packageService.createPackage(cards);

            // Return success response
            return json(Status.CREATED, "Package created successfully");

        } catch (IllegalArgumentException e) {
            // Konflikte wie falsche Eingabedaten oder fehlerhafte Struktur
            return json(Status.CONFLICT, "Invalid package data: " + e.getMessage());
        } catch (Exception e) {
            // Unerwartete Fehler
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    private Response handleAcquirePackage(Request request) {
        try {
            // Validate Authorization header
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return json(Status.UNAUTHORIZED, "Unauthorized access");
            }

            String token = authorization.substring(7); // Extract the token

            packageService.acquirePackage(token);

            // Return success response
            return json(Status.CREATED, "Package acquired successfully");

        } catch (IllegalStateException e) {
            // Konflikte wie keine Münzen oder keine Pakete verfügbar
            return json(Status.CONFLICT, e.getMessage());
        } catch (Exception e) {
            // Unerwartete Fehler
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }
}
