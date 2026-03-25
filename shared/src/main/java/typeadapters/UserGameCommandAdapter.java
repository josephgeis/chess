package typeadapters;

import chess.ChessMove;
import com.google.gson.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import static websocket.commands.UserGameCommand.CommandType;

import java.lang.reflect.Type;

public class UserGameCommandAdapter implements JsonDeserializer<UserGameCommand>, JsonSerializer<UserGameCommand> {
    @Override
    public UserGameCommand deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject root = el.getAsJsonObject();

        CommandType commandType = CommandType.valueOf(root.get("commandType").getAsString());
        String authToken = root.get("authToken").getAsString();
        int gameID = root.get("gameID").getAsInt();

        if (commandType == CommandType.MAKE_MOVE) {
            ChessMove chessMove = ctx.deserialize(root.get("move"), ChessMove.class);
            return new MakeMoveCommand(authToken, gameID, chessMove);
        } else {
            return new UserGameCommand(commandType, authToken, gameID);
        }
    }

    @Override
    public JsonElement serialize(UserGameCommand src, Type typeOfSrc, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();
        root.add("commandType", ctx.serialize(src.getCommandType()));
        root.add("authToken", ctx.serialize(src.getAuthToken()));
        root.add("gameID", ctx.serialize(src.getGameID()));

        if (src instanceof MakeMoveCommand) {
            root.add("move", ctx.serialize(((MakeMoveCommand) src).getMove()));
        }
        return root;
    }
}