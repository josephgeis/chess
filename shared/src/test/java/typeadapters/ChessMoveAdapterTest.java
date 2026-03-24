package typeadapters;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessMoveAdapterTest {

    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessMove.class, new ChessMoveAdapter())
            .create();

    static final ChessPosition POSITION_1_1 = new ChessPosition(1, 1);
    static final ChessPosition POSITION_8_8 = new ChessPosition(8, 8);
    static final ChessMove MOVE = new ChessMove(POSITION_1_1, POSITION_8_8, null);
    static final ChessMove MOVE_PROMOTE = new ChessMove(POSITION_8_8, POSITION_1_1, ChessPiece.PieceType.QUEEN);

    static final String MOVE_STR = """
            { "start": { "row": 1, "col": 1 }, "end": { "row": 8, "col": 8 }, "promotion": null }
            """;
    static final String MOVE_PROMOTE_STR = """
            { "start": { "row": 8, "col": 8 }, "end": { "row": 1, "col": 1 }, "promotion": "QUEEN" }
            """;
    static final String MOVE_INVALID_STR = "{}";

    @Test
    void deserialize() {
        ChessMove testMove = assertDoesNotThrow(() -> gson.fromJson(MOVE_STR, ChessMove.class));
        assertEquals(MOVE, testMove);

        ChessMove testMovePromote = assertDoesNotThrow(() -> gson.fromJson(MOVE_PROMOTE_STR, ChessMove.class));
        assertEquals(MOVE_PROMOTE, testMovePromote);

        ChessMove testMoveInvalid = assertDoesNotThrow(() -> gson.fromJson(MOVE_INVALID_STR, ChessMove.class));
        assertNull(testMoveInvalid);
    }

    @Test
    void serialize() {
        String moveJson = assertDoesNotThrow(() -> gson.toJson(MOVE, ChessMove.class));
        JsonElement moveRoot = gson.fromJson(moveJson, JsonElement.class);
        JsonObject move = moveRoot.getAsJsonObject();
        assertNotNull(move.get("start"));
        assertNotNull(move.get("end"));
        assertNull(move.get("promotion"));
    }
}