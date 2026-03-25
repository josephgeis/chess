package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessManager;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketController {
    WebSocketDispatcher dispatcher;
    WebSocketGameService service;

    Gson gson;

    public WebSocketController(DataAccessManager dataAccessManager, Gson gson) {
        this.gson = gson;
        this.dispatcher = new WebSocketDispatcher(gson);

        this.service = new WebSocketGameService(
                dispatcher,
                dataAccessManager.getAuthDAO(),
                dataAccessManager.getGameDAO());
    }

    public void initWs(WsConfig wsConfig) {
        wsConfig.onConnect(this::onConnect);
        wsConfig.onMessage(this::onMessage);
        wsConfig.onClose(this::onClose);
    }

    public void onConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        dispatcher.addConnection(ctx);
    }

    public void onMessage(WsMessageContext ctx) {
        String rawMessage = ctx.message();
        UserGameCommand command = gson.fromJson(rawMessage, UserGameCommand.class);

        ServerMessage response = service.handleCommand(command, ctx);
        if (response != null) {
            ctx.send(gson.toJson(response));
        }
    }

    public void onClose(WsCloseContext ctx) {
        dispatcher.removeConnection(ctx);
    }
}
