package at.technikum.application.TradingCards.controller;

import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.exception.AuthenticationFailedException;
import at.technikum.application.TradingCards.exception.UserAlreadyExistsException;
import at.technikum.application.TradingCards.exception.UserNotFoundException;
import at.technikum.application.TradingCards.DTO.UserDTO;
import at.technikum.application.TradingCards.service.UserService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class UserController extends Controller {

    private final UserService userService;

    public UserController(UserService userService) {
        super();
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod().name(); // Get the HTTP method as a string

        if ("/users".equals(path) && "POST".equalsIgnoreCase(method)) {
            return handleRegister(request);
        } else if ("/sessions".equals(path) && "POST".equalsIgnoreCase(method)) {
            return handleLogin(request);
        } else {
            return json(Status.NOT_FOUND, "Endpoint not found");
        }
    }

    private Response handleRegister(Request request) {
        Response response = new Response();
        User user = fromBody(request.getBody(), User.class);
        try {
            // Parse request body to UserDTO object
            UserDTO userDTO = userService.createUser(user);

            response = json(Status.CREATED, userDTO);
        } catch (UserAlreadyExistsException e) {
            response.setStatus(Status.CONFLICT);
            response.setBody(e.getMessage());
        } catch (IllegalArgumentException e) {
            response.setStatus(Status.CONFLICT);
            response.setBody("Invalid data: " + e.getMessage());
        }
        return response;
    }

    private Response handleLogin(Request request) {
        Response response = new Response();
        try {
            // Parse request body to UserDTO object
            UserDTO userDTO = fromBody(request.getBody(), UserDTO.class);
            // Debug-Ausgabe
            System.out.println("Parsed UserDTO: username=" + userDTO.getUsername() + ", password=" + userDTO.getPassword());

            // Benutzer authentifizieren
            String token = userService.authenticate(userDTO);
            response.setStatus(Status.OK);
            response.setBody(token);
        } catch (AuthenticationFailedException e) {
            response.setStatus(Status.UNAUTHORIZED);
            response.setBody(e.getMessage());
        } catch (UserNotFoundException e) {
            response.setStatus(Status.NOT_FOUND);
            response.setBody(e.getMessage());
        }
        return response;
    }
}
