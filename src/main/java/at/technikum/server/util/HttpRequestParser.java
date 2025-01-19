package at.technikum.server.util;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;

public class HttpRequestParser {

    // http req string to java obj
    public Request parse(String http) {
        Request request = new Request();
        request.setHttp(http);

        String[] lines = http.split("\r\n");
        String requestLine = lines[0];
        String[] requestLineParts = requestLine.split(" ");

        request.setMethod(Method.valueOf(requestLineParts[0]));

        // Parse path and query parameters
        String fullPath = requestLineParts[1];
        String[] pathParts = fullPath.split("\\?", 2); // Split into path and query
        request.setPath(pathParts[0]); // Set the path

        if (pathParts.length > 1) {
            parseQueryParameters(pathParts[1], request);
        }

        // Parse headers
        int emptyLine = 0;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            if (line.isEmpty()) {
                emptyLine = i;
                break;
            }

            String[] headerParts = line.split(":", 2);
            request.setHeader(headerParts[0], headerParts[1].trim());
        }

        if (emptyLine == 0 || lines.length - 1 == emptyLine) {
            return request;
        }

        // Parse body
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = emptyLine + 1; i < lines.length; i++) {
            bodyBuilder.append(lines[i]);

            if (lines.length - 1 != i) {
                bodyBuilder.append("\r\n");
            }
        }

        request.setBody(bodyBuilder.toString());

        return request;
    }

    // Helper function to parse query parameters and add them to the Request object
    private void parseQueryParameters(String queryString, Request request) {
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : ""; // Handle parameters without values
            request.setQueryParameter(key, value);
        }
    }
}
