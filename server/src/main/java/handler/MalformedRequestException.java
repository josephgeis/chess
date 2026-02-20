package handler;

public class MalformedRequestException extends InvalidRequestException {
    public MalformedRequestException() {
        super(400, "bad request");
    }
}
