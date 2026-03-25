package websocket;

import chess.ChessMove;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

public class WebSocketGameService {
    private final WebSocketDispatcher dispatcher;

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketGameService(WebSocketDispatcher dispatcher, AuthDAO authDAO, GameDAO gameDAO) {
        this.dispatcher = dispatcher;

        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    /**
     * Handles a command parsed from an incoming WebSockets message.
     * @param command Input command from the client
     * @param ctx WebSocket connection context
     * @return Returns a message to send back to root client
     */
    public ServerMessage handleCommand(UserGameCommand command, WsContext ctx) {
        try {
            AuthData authData = authDAO.retrieveAuth(command.getAuthToken());
            GameData gameData = gameDAO.retrieveGame(command.getGameID());

            return switch (command.getCommandType()) {
                case CONNECT -> onConnectGame(authData, gameData, ctx);
                case LEAVE -> onLeaveGame(authData, gameData, ctx);
                case RESIGN -> onResignGame(authData, gameData, ctx);
                case MAKE_MOVE -> onMakeMove(authData, gameData, ((MakeMoveCommand) command).getMove(), ctx);
            };
        } catch (Exception e) {
            return new ErrorMessage(e.getMessage());
        }
    }

    private ServerMessage onConnectGame(AuthData authData, GameData gameData, WsContext ctx) {
        throw new RuntimeException("Not implemented.");
    }

    private ServerMessage onLeaveGame(AuthData authData, GameData gameData, WsContext ctx) {
        throw new RuntimeException("Not implemented.");
    }

    private ServerMessage onResignGame(AuthData authData, GameData gameData, WsContext ctx) {
        throw new RuntimeException("Not implemented.");
    }

    private ServerMessage onMakeMove(AuthData authData, GameData gameData, ChessMove move, WsContext ctx) {
        throw new RuntimeException("Not implemented.");
    }
}
