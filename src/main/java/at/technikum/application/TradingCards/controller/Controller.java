package at.technikum.application.TradingCards.controller;
import at.technikum.application.TradingCards.exception.InvalidBodyException;
import at.technikum.application.TradingCards.exception.JsonParserException;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Controller {
    private final ObjectMapper objectMapper;

    public Controller() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(
                MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true
        );
    }

    public abstract Response handle(Request request);

    protected <T> T fromBody(String body, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(body, typeReference);
        } catch (JsonProcessingException e) {
            throw new InvalidBodyException(e);
        }
    }

    protected <T> T fromBody(String body, Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new InvalidBodyException(e);
        }
    }

    protected Response json(Status status, Object object) {
        Response response = new Response();
        response.setStatus(status);
        response.setHeader("Content-Type", "application/json");
        try {
            response.setBody(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e);
        }

        return response;
    }
}
