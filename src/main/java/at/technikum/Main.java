package at.technikum;

import at.technikum.application.MCTG_Application;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.UserRepository;
import at.technikum.application.echo.EchoApplication;
import at.technikum.application.html.SimpleHtmlApplication;
import at.technikum.server.Server;
import at.technikum.server.http.Request;

import java.util.Collection;

public class Main {
    public static void main(String[] args) {



        Server server = new Server(new MCTG_Application());
        server.start();
    }
}