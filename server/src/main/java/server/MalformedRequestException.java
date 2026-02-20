package server;

public class MalformedRequestException extends RequestException {
    public MalformedRequestException() {
        super(400, "bad request");
    }
}
