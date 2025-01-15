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

        UserRepository userRepository = new UserRepository();

        // Benutzer hinzufügen
        userRepository.save(new User("kienboec", "password1"));
        userRepository.save(new User("altenhof", "password2"));

        // Alle Benutzer abrufen und ausgeben
        Collection<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            System.out.println(user); // Gibt die `toString`-Methode des Benutzers aus
        }

        User user3 = userRepository.findByUsername("kienboec");
        System.out.println(user3);

        Server server = new Server(new MCTG_Application());
        server.start();
    }
}