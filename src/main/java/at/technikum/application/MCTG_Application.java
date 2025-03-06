package at.technikum.application;

import at.technikum.application.TradingCards.controller.*;
import at.technikum.application.TradingCards.exception.ControllerNotFoundException;
import at.technikum.application.TradingCards.repository.*;
import at.technikum.application.TradingCards.router.Router;
import at.technikum.application.TradingCards.service.*;
import at.technikum.application.data.ConnectionPool;
import at.technikum.application.data.DatabaseCleaner;
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
        System.out.println("Response: " + response.getBody());
        return response;
    }

    private void initRoutes(){
        ConnectionPool connectionPool = new ConnectionPool();

        UserRepository userRepository = new UserDbRepository(connectionPool);
        CardRepository cardRepository = new CardDbRepository(connectionPool);
        PackageRepository packageRepository = new PackageDbRepository(connectionPool);
        DeckRepository deckRepository = new DeckDbRepository(connectionPool);
        StatsRepository statsRepository = new StatsDbRepository(connectionPool);
        ScoreboardRepository scoreboardRepository = new ScoreboardDbRepository(connectionPool);


        UserService userService = new UserService(userRepository,statsRepository);
        CardService cardService = new CardService(cardRepository,userRepository);
        PackageService packageService = new PackageService(packageRepository, userRepository,cardService);
        DeckService deckService = new DeckService(deckRepository,cardService,cardRepository);
        BattleService battleService = new BattleService(cardRepository,statsRepository,userRepository,deckRepository);
        StatsService statsService = new StatsService(statsRepository,userRepository);
        ScoreboardService scoreboardService = new ScoreboardService(scoreboardRepository);

        PackageController packageController = new PackageController(packageService,userService);
        UserController userController = new UserController(userService);
        CardController cardController = new CardController(cardService,userService);
        DeckController deckController = new DeckController(deckService,userService);
        BattleController battleController = new BattleController(battleService,userService);
        StatsController statsController = new StatsController(statsService,userService);
        ScoreboardController scoreboardController = new ScoreboardController(scoreboardService,userService);

        DatabaseCleaner databaseCleaner = new DatabaseCleaner(connectionPool);

        databaseCleaner.clearAllTables(); // This will clear all entries in the database

        this.router.addRoute("/sessions", userController);
        this.router.addRoute("/users", userController);
        this.router.addRoute("/packages", packageController);
        this.router.addRoute("/transactions/packages", packageController);
        this.router.addRoute("/cards", cardController);
        this.router.addRoute("/deck", deckController);
        this.router.addRoute("/battle", battleController);
        this.router.addRoute("/stats", statsController);
        this.router.addRoute("/scoreboard", scoreboardController);




    }


}
