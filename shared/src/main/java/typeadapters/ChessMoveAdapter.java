package typeadapters;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessMoveAdapter implements JsonDeserializer<ChessMove>, JsonSerializer<ChessMove> {
    @Override
    public ChessMove deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject root = el.getAsJsonObject();
        JsonElement startEl = root.get("start");
        JsonElement endEl = root.get("end");
        ChessPosition start = ctx.deserialize(startEl, ChessPosition.class);
        ChessPosition end = ctx.deserialize(endEl, ChessPosition.class);
        JsonElement promotionEl = root.get("promotion");
        String promotion = promotionEl != null ? promotionEl.getAsString() : null;

        return new ChessMove(
                start, end,
                promotion != null ? ChessPiece.PieceType.valueOf(promotion) : null
        );
    }

    @Override
    public JsonElement serialize(ChessMove chessMove, Type type, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();
        JsonElement start = ctx.serialize(chessMove.getStartPosition(), ChessPosition.class);
        JsonElement end = ctx.serialize(chessMove.getStartPosition(), ChessPosition.class);
        JsonElement promotion = ctx.serialize(chessMove.getPromotionPiece(), ChessPiece.PieceType.class);

        root.add("start", start);
        root.add("end", end);
        root.add("promotion", promotion);

        return root;
    }
}
