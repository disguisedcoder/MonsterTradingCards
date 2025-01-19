package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.service.PackageService;
import at.technikum.application.TradingCards.service.UserService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class PackageController extends Controller {

    private final PackageService packageService;
    private final UserService userService;

    public PackageController(PackageService packageService, UserService userService) {
        this.packageService = packageService;
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod().name();

        // Determine route
        switch (path) {
            case "/packages":
                if ("POST".equalsIgnoreCase(method)) {
                    return handleCreatePackage(request);
                }
                break;

            case "/transactions/packages":
                if ("POST".equalsIgnoreCase(method)) {
                    return handleAcquirePackage(request);
                }
                break;

            default:
                return json(Status.NOT_FOUND, "Endpoint not found");
        }

        return json(Status.NOT_FOUND, "Endpoint not found");
    }

    private Response handleCreatePackage(Request request) {
        try {
            // Validate user authorization
            String token = request.getHeader("Authorization");
            userService.validateAdmin(token);

            // Parse JSON body into a list of Card objects
            List<Card> cards = fromBody(request.getBody(), new TypeReference<>() {});

            // Delegate package creation to the service
            packageService.createPackage(cards);

            return json(Status.CREATED, "Package created successfully");
        } catch (IllegalArgumentException e) {
            return json(Status.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    private Response handleAcquirePackage(Request request) {
        try {
            // Validate user authorization and extract the token
            String token = userService.validateToken(request.getHeader("Authorization"));

            // Delegate package acquisition to the service
            packageService.acquirePackage(token);

            return json(Status.CREATED, "Package acquired successfully");
        } catch (IllegalArgumentException e) {
            return json(Status.UNAUTHORIZED, e.getMessage());
        } catch (IllegalStateException e) {
            return json(Status.CONFLICT, e.getMessage());
        } catch (Exception e) {
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }
}
