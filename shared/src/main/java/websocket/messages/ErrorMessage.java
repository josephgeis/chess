package websocket.messages;

public class ErrorMessage extends ServerMessage implements PresentableMessage {
    String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = "Error: %s".formatted(errorMessage);
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}
