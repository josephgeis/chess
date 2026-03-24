package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import service.ServiceManager;
import websocket.commands.UserGameCommand;

public class WebSocketController {
    ServiceManager serviceManager;
    Gson gson;

    public WebSocketController(ServiceManager serviceManager, Gson gson) {
        this.serviceManager = serviceManager;
        this.gson = gson;
    }

    public void initWs(WsConfig wsConfig) {
        wsConfig.onConnect(this::onConnect);
        wsConfig.onMessage(this::onMessage);
        wsConfig.onClose(this::onClose);
    }

    public void onConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("Opened ws collected");
    }

    public void onMessage(WsMessageContext ctx) {
        String rawMessage = ctx.message();
        System.out.println("M: " + rawMessage);
        UserGameCommand message =  gson.fromJson(rawMessage, UserGameCommand.class);
        ctx.send(gson.toJson(message));
    }

    public void onClose(WsCloseContext ctx) {
        System.out.println("Closed connection.");
    }
}
