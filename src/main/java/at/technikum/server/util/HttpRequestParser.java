package at.technikum.server.util;

import at.technikum.server.http.Request;

public class HttpRequestParser {

    // http req string to java obj

    public Request parse (String http){

        http= """
                GET /users HTTP/1.1
                Host: localhost
                
                """;

            Request request = new Request();
            String lines[]= http.split("\\s+");


        return request;
    }
}
