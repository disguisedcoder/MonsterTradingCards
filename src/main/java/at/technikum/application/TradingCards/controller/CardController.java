package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.DTO.CardDTO;
import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.service.CardService;
import at.technikum.application.TradingCards.service.UserService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class CardController extends Controller {

    private final CardService cardService;
    private final UserService userService;

    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod().name();

        if ("/cards".equals(path) && "GET".equalsIgnoreCase(method)) {
            return handleGetAllCards(request);
        }

        return json(Status.NOT_FOUND, "Endpoint not found");
    }

    private Response handleGetAllCards(Request request) {
        try {
            // Validate user authorization
            String token = userService.validateToken(request.getHeader("Authorization"));

            // Fetch all cards for the authenticated user
            List<CardDTO> cardDTOs = cardService.getCardDTOsByUserToken(token);

            return json(Status.OK, cardDTOs);
        } catch (IllegalArgumentException e) {
            return json(Status.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }
}
