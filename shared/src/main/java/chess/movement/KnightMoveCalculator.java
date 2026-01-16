package chess.movement;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

/**
 * MoveCalculator subclass for knights.
 * Knights can move in an L shape.
 */
public class KnightMoveCalculator extends MoveCalculator {
    public KnightMoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        super(piece, startPosition, board);
        this.directions = new MoveDelta[]{
                new MoveDelta(2, 1),
                new MoveDelta(2, -1),
                new MoveDelta(1, 2),
                new MoveDelta(1, -2),
                new MoveDelta(-2, 1),
                new MoveDelta(-2, -1),
                new MoveDelta(-1, 2),
                new MoveDelta(-1, -2),
        };
    }
}
