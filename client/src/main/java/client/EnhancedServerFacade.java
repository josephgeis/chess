package client;

import com.google.gson.GsonBuilder;
import jakarta.websocket.MessageHandler;
import typeadapters.ServerMessageAdapter;
import typeadapters.UserGameCommandAdapter;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class EnhancedServerFacade extends ServerFacade {
    ChessWsClient wsClient;

    Set<MessageObserver> messageObservers;
    Logger logger;

    public EnhancedServerFacade(ChessHttpClient httpClient, ChessWsClient wsClient) {
        super(httpClient);
        this.gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(UserGameCommand.class,
                        new UserGameCommandAdapter())
                .registerTypeAdapter(ServerMessage.class,
                        new ServerMessageAdapter())
                .create();
        this.logger = Logger.getLogger("EnhancedServerFacade");
        this.wsClient = wsClient;
        this.wsClient.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                logger.info("Received message: %s".formatted(s));
                EnhancedServerFacade.this.notifyObservers(s);
            }
        });
        this.messageObservers = new HashSet<>();
    }

    public CompletableFuture<Void> sendWsCommand(UserGameCommand command) {
        String message = gson.toJson(command);
        return wsClient.sendMessageAsync(message);
    }

    public void notifyObservers(String s) {
        ServerMessage message = gson.fromJson(s, ServerMessage.class);
        messageObservers.forEach(observer -> {
            logger.fine("Notifying observer: %s".formatted(observer));
            observer.notify(message);
        });
    }

    public void registerMessageObserver(MessageObserver observer) {
        logger.fine("Registered observer: %s".formatted(observer));
        messageObservers.add(observer);
    }

    public void unregisterMessageObserver(MessageObserver observer) {
        logger.fine("Unregistered observer: %s".formatted(observer));
        messageObservers.remove(observer);
    }

}
