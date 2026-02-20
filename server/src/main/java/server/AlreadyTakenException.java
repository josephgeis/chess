package server;

public class AlreadyTakenException extends RequestException {
    public AlreadyTakenException() {
        super(403,"Already Taken");
    }
}
