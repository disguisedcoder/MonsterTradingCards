package at.technikum.server.http;

public enum Status {

    OK(200, "OK"),
    CREATED(201, "CREATED"),
    BAD_REQUEST(400, "Bad Request"),
    NO_CONTENT(204, "No Content"),
    NOT_FOUND(404, "Not Found"),
    UNAUTHORIZED(401, "Unauthorized Access"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}