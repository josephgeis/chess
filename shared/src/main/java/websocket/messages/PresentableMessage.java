package websocket.messages;

public interface PresentableMessage {
    ServerMessage.ServerMessageType getServerMessageType();
    String getMessage();
}
