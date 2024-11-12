package at.technikum.server.util;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;

public class HttpRequestParser {

    // http req string to java obj

    // http req string to java obj
    public Request parse(String http) {
        Request request = new Request();

        String[] lines = http.split("\r\n");
        String requestLine = lines[0];
        String[] requestLineParts = requestLine.split(" ");

        request.setMethod(Method.valueOf(requestLineParts[0]));
        request.setPath(requestLineParts[1]);

        // parse headers
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            if (line.isEmpty()) {
                break;
            }

            String[] headerParts = line.split(":", 2);
            request.setHeader(headerParts[0], headerParts[1].trim());
        }




        return request;
    }
}
