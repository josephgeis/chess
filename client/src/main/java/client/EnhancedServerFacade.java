package client;

import websocket.commands.UserGameCommand;

import java.util.concurrent.CompletableFuture;

public class EnhancedServerFacade extends ServerFacade {
    ChessWsClient wsClient;

    public EnhancedServerFacade(ChessHttpClient httpClient, ChessWsClient wsClient) {
        super(httpClient);
        this.wsClient = wsClient;
    }

    public CompletableFuture<Void> sendWsCommand(UserGameCommand command) {
        String message = gson.toJson(command);
        return wsClient.sendMessageAsync(message);
    }

}
