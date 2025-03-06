package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.service.ScoreboardService;
import at.technikum.application.TradingCards.service.UserService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import java.util.List;

public class ScoreboardController extends Controller {

    private final ScoreboardService scoreboardService;
    private final UserService userService;

    public ScoreboardController(ScoreboardService scoreboardService, UserService userService) {
        this.scoreboardService = scoreboardService;
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        if ("GET".equalsIgnoreCase(request.getMethod().name()) && "/scoreboard".equalsIgnoreCase(request.getPath())) {
            try {
                String token = userService.validateToken(request.getHeader("Authorization"));
                List scoreboard = scoreboardService.getScoreboard();
                return json(Status.OK, scoreboard);
            } catch (Exception e) {
                return json(Status.UNAUTHORIZED, e.getMessage());
            }
        }
        return json(Status.NOT_FOUND, "Endpoint not found");
    }
}
