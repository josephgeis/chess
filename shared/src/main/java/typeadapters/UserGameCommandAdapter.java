package typeadapters;

import com.google.gson.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.lang.reflect.Type;

public class UserGameCommandAdapter implements JsonDeserializer<UserGameCommand>, JsonSerializer<UserGameCommand> {
    @Override
    public UserGameCommand deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        if (!el.isJsonObject()) {
            return null;
        }

        String commmandTypeString = el.getAsJsonObject().get("commandType").getAsString();
        UserGameCommand.CommandType commandType = UserGameCommand.CommandType.valueOf(commmandTypeString);
        if (commandType == UserGameCommand.CommandType.MAKE_MOVE) {
            return ctx.deserialize(el, MakeMoveCommand.class);
        } else {
            return ctx.deserialize(el, UserGameCommand.class);
        }
    }

    @Override
    public JsonElement serialize(UserGameCommand src, Type typeOfSrc, JsonSerializationContext ctx) {
        if (src instanceof MakeMoveCommand) {
            return ctx.serialize(src, MakeMoveCommand.class);
        } else {
            return ctx.serialize(src);
        }
    }
}