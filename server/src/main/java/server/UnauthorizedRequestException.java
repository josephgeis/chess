package server;

public class UnauthorizedRequestException extends RequestException {
    public UnauthorizedRequestException() {
        super(401, "unauthorized");
    }
}