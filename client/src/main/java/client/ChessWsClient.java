package client;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class ChessWsClient extends Endpoint {

    Session session;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) { }

    public ChessWsClient(String host, int port) throws Exception {
        URI uri = new URI("ws", null, host, port, "/ws", null, null);

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);
    }

    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public CompletableFuture<Void> sendMessageAsync(String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addMessageHandler(MessageHandler messageHandler) {
        session.addMessageHandler(messageHandler);
    }
}
