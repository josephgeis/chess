package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = "Error: %s".formatted(errorMessage);
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
