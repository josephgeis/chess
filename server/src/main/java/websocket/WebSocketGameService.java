package websocket;

import chess.ChessMove;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
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

    /** CONNECT command
     * 1. Server sends a LOAD_GAME message back to the root client.
     * 2. Server sends a Notification message to all other clients in that game informing them the root client connected to the game,
     * either as a player (in which case their color must be specified) or as an observer.
     * @return LoadGameMessage to root client
     */
    private ServerMessage onConnectGame(AuthData authData, GameData gameData, WsContext ctx) {
        String username = authData.username();
        String role;
        if (gameData.whiteUsername().equals(username)) {
            role = "White";
        } else if (gameData.blackUsername().equals(username)) {
            role = "Black";
        } else {
            role = "Observer";
        }

        NotificationMessage notificationMessage = new NotificationMessage("%s (%s) joined the game".formatted(username, role));
        WebSocketChannel channel = dispatcher.channelForGame(gameData.gameID());
        channel.publishMessage(notificationMessage);

        dispatcher.subscribeToGame(gameData.gameID(), ctx);
        return new LoadGameMessage(gameData);
    }

    private ServerMessage onLeaveGame(AuthData authData, GameData gameData, WsContext ctx) throws DataAccessException {
        String username = authData.username();
        String role = "Observer";
        if (gameData.whiteUsername().equals(username)) {
            role = "White";
            gameData.setWhiteUsername(null);
        } else if (gameData.blackUsername().equals(username)) {
            role = "Black";
            gameData.setBlackUsername(null);
        }

        if (!role.equals("Observer")) {
            gameDAO.updateGame(gameData.gameID(), gameData);
        }

        dispatcher.unsubscribeFromGame(gameData.gameID(), ctx);

        WebSocketChannel channel = dispatcher.channelForGame(gameData.gameID());
        NotificationMessage notificationMessage = new NotificationMessage("%s (%s) joined the game".formatted(username, role));
        channel.publishMessage(notificationMessage);
        return null;
    }

    private ServerMessage onResignGame(AuthData authData, GameData gameData, WsContext ctx) {
        throw new RuntimeException("Not implemented.");
    }

    private ServerMessage onMakeMove(AuthData authData, GameData gameData, ChessMove move, WsContext ctx) {
        throw new RuntimeException("Not implemented.");
    }
}
