package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;

public class WebSocketChannel {
    Set<WsContext> subscribers;
    Gson gson;

    public WebSocketChannel(Gson gson) {
        this.subscribers = new HashSet<>();
        this.gson = gson;
    }

    public void subscribe(WsContext subscriber) {
        this.subscribers.add(subscriber);
    }

    public void unsubscribe(WsContext subscriber) {
        this.subscribers.remove(subscriber);
    }

    public CompletableFuture<Void> publishMessage(ServerMessage message) {
        String payload = gson.toJson(message);
        Function<WsContext, CompletableFuture<Void>> messagePublisher = ctx -> CompletableFuture.runAsync(() -> ctx.send(payload));
        List<CompletableFuture<Void>> futures = subscribers.stream()
                .map(messagePublisher)
                .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
