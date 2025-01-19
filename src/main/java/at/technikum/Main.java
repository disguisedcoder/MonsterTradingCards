package at.technikum;

import at.technikum.application.MCTG_Application;
import at.technikum.server.Server;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(new MCTG_Application());
        server.start();
    }
}