package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.DTO.CardDTO;
import at.technikum.application.TradingCards.service.DeckService;
import at.technikum.application.TradingCards.service.UserService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class DeckController extends Controller {
    private final DeckService deckService;
    private final UserService userService;

    public DeckController(DeckService deckService, UserService userService) {
        this.deckService = deckService;
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        String method = request.getMethod().name();

            if ("GET".equalsIgnoreCase(method)) {
                return handleGetDeck(request);
            } else if ("PUT".equalsIgnoreCase(method)) {
                return handleConfigureDeck(request);
            }

        return json(Status.NOT_FOUND, "Endpoint not found");
    }

    private Response handleGetDeck(Request request) {

        try {
            String token = userService.validateToken(request.getHeader("Authorization"));
            String format = request.getQueryParameter("format");
            System.out.println(format);
            if (format != null && "plain".equalsIgnoreCase(format)) {
                // Plain format: id, name, and damage
                List<CardDTO> plainDeck = deckService.getDeckByToken(token);
                //System.out.println("i was here");
                return json(Status.OK, plainDeck.isEmpty() ? "[]" : plainDeck);
            } else {
                // Default: Only card IDs
                List<CardDTO> deck = deckService.getDeckByToken(token);
                List<String> cardIds = deckService.extractCardIds(deck); // Verwende die Service-Methode
                return json(Status.OK, deck.isEmpty() ? "[]" : cardIds);
            }
        } catch (IllegalArgumentException e) {
            return json(Status.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    private Response handleConfigureDeck(Request request) {
        try {
            String token = userService.validateToken(request.getHeader("Authorization"));
            List<String> cardIds = fromBody(request.getBody(), List.class);

            // Validate number of cards
            if (cardIds.size() != 4) {
                return json(Status.BAD_REQUEST, "A deck must contain exactly 4 cards.");
            }

            deckService.configureDeck(token, cardIds);
            return json(Status.OK, "Deck configured successfully.");
        } catch (IllegalArgumentException e) {
            return json(Status.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }
}
