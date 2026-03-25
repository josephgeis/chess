package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import service.ServiceManager;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class WebSocketController {
    WebSocketDispatcher dispatcher;

    ServiceManager serviceManager;
    Gson gson;

    public WebSocketController(ServiceManager serviceManager, Gson gson) {
        this.gson = gson;
        this.serviceManager = serviceManager;
        dispatcher = new WebSocketDispatcher(gson);
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
        UserGameCommand userGameCommand = gson.fromJson(rawMessage, UserGameCommand.class);

        switch (userGameCommand.getCommandType()) {
            case CONNECT -> onConnectGame(userGameCommand, ctx);
            case LEAVE -> onLeaveGame(userGameCommand, ctx);
            case RESIGN -> onResignGame(userGameCommand, ctx);
            case MAKE_MOVE -> onMakeMove((MakeMoveCommand) userGameCommand, ctx);
        }
    }

    public void onClose(WsCloseContext ctx) {
        dispatcher.removeConnection(ctx);
    }

    private void onConnectGame(UserGameCommand command, WsContext ctx) {
        assert command.getCommandType() == UserGameCommand.CommandType.CONNECT;
        dispatcher.subscribeToGame(command.getGameID(), ctx);
    }

    private void onLeaveGame(UserGameCommand command, WsMessageContext ctx) {
        assert command.getCommandType() == UserGameCommand.CommandType.LEAVE;
        dispatcher.unsubscribeFromGame(command.getGameID(), ctx);
    }

    private void onResignGame(UserGameCommand command, WsMessageContext ctx) {
        assert command.getCommandType() == UserGameCommand.CommandType.RESIGN;
    }

    private void onMakeMove(MakeMoveCommand command, WsMessageContext ctx) {
        assert command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE;
    }
}
