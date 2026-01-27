package chess.movement;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class BishopMoveCalculator extends QueenMoveCalculator {
    public BishopMoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        super(piece, startPosition, board);
        this.directions = new MoveDelta[]{
                new MoveDelta(1, 1),
                new MoveDelta(1, -1),
                new MoveDelta(-1, -1),
                new MoveDelta(-1, 1)
        };
    }
}
