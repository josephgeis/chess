package chess.movement;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

/**
 * MoveCalculator subclass for kings.
 * Kings can move 1 space in any direction.
 */
public class KingMoveCalculator extends MoveCalculator {

    public KingMoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        super(piece, startPosition, board);
        this.directions = new MoveDelta[]{
                new MoveDelta(1, 0),
                new MoveDelta(1, 1),
                new MoveDelta(0, 1),
                new MoveDelta(-1, 1),
                new MoveDelta(-1, 0),
                new MoveDelta(-1, -1),
                new MoveDelta(0, -1),
                new MoveDelta(1, -1)
        };
    }
}
