package at.technikum.application;

import at.technikum.application.TradingCards.controller.Controller;
import at.technikum.application.TradingCards.controller.UserController;
import at.technikum.application.TradingCards.exception.ControllerNotFoundException;
import at.technikum.application.TradingCards.router.Router;
import at.technikum.server.Application;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class MCTG_Application implements Application {


    private final  Router router;

    public MCTG_Application() {
        this.router = new Router();
        this.initRoutes();
    }

    @Override
    public Response handle(Request request) {
        Response response = new Response();
        try{
            Controller controller = this.router.getController(request.getPath());
            response = controller.handle(request);
        }catch (ControllerNotFoundException e){
            response.setStatus(Status.NOT_FOUND);
            response.setHeader("Content-Type", "text/html");
            response.setBody("<h1> " + e.getMessage() +  "</h1>");
        }
        return response;
    }

    private void initRoutes(){
        UserController userController = new UserController();

        this.router.addRoute("/sessions", userController);
        this.router.addRoute("/users", userController);

    }


}
