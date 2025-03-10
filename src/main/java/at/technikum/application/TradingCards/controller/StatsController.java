package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.service.StatsService;
import at.technikum.application.TradingCards.service.UserService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class StatsController extends Controller {

    private final StatsService statsService;
    private final UserService userService;

    public StatsController(StatsService statsService, UserService userService) {
        this.statsService = statsService;
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        String method = request.getMethod().name();
        String path = request.getPath();

        if ("GET".equalsIgnoreCase(method)) {
            if (path.equals("/stats")) {
                return handleGetStats(request);
            } 
        }

        return json(Status.NOT_FOUND, "Endpoint not found");
    }

    private Response handleGetStats(Request request) {
        try {
            String token = userService.validateToken(request.getHeader("Authorization"));
            var stats = statsService.getStats(token);
            return json(Status.OK, stats);
        } catch (Exception e) {
            return json(Status.UNAUTHORIZED, e.getMessage());
        }
    }


}
