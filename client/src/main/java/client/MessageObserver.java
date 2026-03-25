package client;

import websocket.messages.ServerMessage;

public interface MessageObserver {
    void notify(ServerMessage message);
}
