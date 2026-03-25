package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSocketDispatcher {
    Map<Integer, WebSocketChannel> gameChannels;
    Map<WsContext, Set<WebSocketChannel>> connections;

    Gson gson;

    public WebSocketDispatcher(Gson gson) {
        gameChannels = new HashMap<>();
        connections = new HashMap<>();
        this.gson = gson;
    }

    public void addConnection(WsContext ctx) {
        connections.putIfAbsent(ctx, new HashSet<>());
    }

    public void removeConnection(WsContext ctx) {
        Set<WebSocketChannel> subscriptions = connections.remove(ctx);
        if (subscriptions != null) {
            subscriptions.forEach(channel -> channel.unsubscribe(ctx));
        }
    }

    public WebSocketChannel subscribeToGame(int gameID, WsContext ctx) {
        WebSocketChannel channel = channelForGame(gameID);
        channel.subscribe(ctx);
        connections.get(ctx).add(channel);
        return channel;
    }

    public WebSocketChannel unsubscribeFromGame(int gameID, WsContext ctx) {
        WebSocketChannel channel = gameChannels.get(gameID);
        if (channel != null) {
            channel.unsubscribe(ctx);
            connections.get(ctx).remove(channel);
        }
        return channel;
    }

    public WebSocketChannel channelForGame(int gameID) {
        return gameChannels.computeIfAbsent(gameID, k -> new WebSocketChannel(gson));
    }
}
