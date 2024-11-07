package at.technikum.server;

import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public interface Application {

    Response handle(Request request);
}
