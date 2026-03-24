package typeadapters;

import com.google.gson.*;
import websocket.messages.*;

import java.lang.reflect.Type;

public class ServerMessageAdapter implements JsonDeserializer<ServerMessage>, JsonSerializer<ServerMessage> {
    @Override
    public ServerMessage deserialize(JsonElement el, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        if (!el.isJsonObject()) {
            return null;
        }

        String serverMessageType = el.getAsJsonObject().get("serverMessageType").getAsString();
        ServerMessage.ServerMessageType messageType = ServerMessage.ServerMessageType.valueOf(serverMessageType);


        return switch (messageType) {
            case ERROR -> ctx.deserialize(el, ErrorMessage.class);
            case LOAD_GAME -> ctx.deserialize(el, LoadGameMessage.class);
            case NOTIFICATION -> ctx.deserialize(el, NotificationMessage.class);
        };
    }

    @Override
    public JsonElement serialize(ServerMessage src, Type typeOfSrc, JsonSerializationContext ctx) {
        return switch (src.getServerMessageType()) {
            case ERROR -> ctx.serialize(src, ErrorMessage.class);
            case LOAD_GAME -> ctx.serialize(src, LoadGameMessage.class);
            case NOTIFICATION -> ctx.serialize(src, NotificationMessage.class);
        };
    }
}
