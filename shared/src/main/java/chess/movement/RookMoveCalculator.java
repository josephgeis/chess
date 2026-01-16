package chess.movement;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class RookMoveCalculator extends RadiatingMoveCalculator {
    public RookMoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        super(piece, startPosition, board);
        this.directions = new MoveDelta[]{
                new MoveDelta(0, 1),
                new MoveDelta(1, 0),
                new MoveDelta(0, -1),
                new MoveDelta(-1, 0)
        };
    }
}
