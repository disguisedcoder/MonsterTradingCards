package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.service.BattleService;
import at.technikum.application.TradingCards.service.UserService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class BattleController extends Controller {

    private final BattleService battleService;
    private final UserService userService;

    public BattleController(BattleService battleService, UserService userService) {
        this.battleService = battleService;
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        String method = request.getMethod().name();
        String path = request.getPath();

        if ("/battles".equalsIgnoreCase(path)) {
            if ("POST".equalsIgnoreCase(method)) {
                return handleBattleRequest(request);
            } else if ("GET".equalsIgnoreCase(method)) {
                return getBattleResult(request);
            }
        }

        return json(Status.NOT_FOUND, "Endpoint not found");
    }

    /**
     * Handles adding a player to the battle queue.
     */
    private Response handleBattleRequest(Request request) {
        try {
            String token = userService.validateToken(request.getHeader("Authorization"));
            battleService.addPlayerToQueue(token);
            String battleLog = battleService.getBattleResult(token);
            if (battleLog != null) {
                return json(Status.OK, battleLog);
            }
            return json(Status.OK, "Player added to the battle queue. Waiting for an opponent...");
        } catch (IllegalArgumentException e) {
            return json(Status.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves the result of a completed battle.
     */
    private Response getBattleResult(Request request) {
        try {
            String token = userService.validateToken(request.getHeader("Authorization"));
            String battleLog = battleService.getBattleResult(token);

            if (battleLog == null) {
                return json(Status.OK, "No battle result available yet. Please wait...");
            }

            return json(Status.OK, battleLog);
        } catch (IllegalArgumentException e) {
            return json(Status.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return json(Status.INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }
}
