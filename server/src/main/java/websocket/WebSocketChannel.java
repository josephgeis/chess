package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.logging.Logger;

public class WebSocketChannel {
    Set<WsContext> subscribers;
    Gson gson;
    Logger logger = Logger.getLogger("WebSocketChannel");

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
        return publishMessage(message, null);
    }

    public CompletableFuture<Void> publishMessage(ServerMessage message, Set<WsContext> exclude) {
        Collection<WsContext> recipients;

        if (exclude == null) {
            recipients = subscribers;
        } else {
            recipients = subscribers.stream().filter(subscriber -> !exclude.contains(subscriber)).toList();
        }

        String payload = gson.toJson(message);
        logger.info("Publish message: %s".formatted(payload));
        Function<WsContext, CompletableFuture<Void>> messagePublisher = ctx -> CompletableFuture.runAsync(() -> ctx.send(payload));
        List<CompletableFuture<Void>> futures = recipients.stream()
                .map(messagePublisher)
                .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
