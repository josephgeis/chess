package websocket.messages;

public class NotificationMessage extends ServerMessage implements PresentableMessage {
    String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
