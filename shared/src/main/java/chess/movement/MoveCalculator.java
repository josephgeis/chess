package chess.movement;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.Vector;

/**
 * Used to
 */
public abstract class MoveCalculator {

    ChessPiece piece;
    ChessPosition startPosition;
    final ChessBoard board;
    MoveDelta [] directions;

    public MoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.board = board;
    }

    public Collection<ChessMove> calculateMoves() {
        var validMoves = new Vector<ChessMove>();

        ChessPosition endPosition;
        ChessPiece pieceAtPosition;
        for (MoveDelta direction : directions) {
            endPosition = direction.addTo(startPosition);

            // Check if move is valid:
            // 1. endPosition is in bounds (if not, skip immediately; don't get piece there)
            if (!endPosition.inBounds()) continue;

            // 2. no piece in newPosition, or the piece there is an opposing piece.
            pieceAtPosition = board.getPiece(endPosition);
            if (pieceAtPosition == null || pieceAtPosition.getTeamColor() != piece.getTeamColor())
                validMoves.add(new ChessMove(startPosition, endPosition, null));
        }

        return validMoves;
    }
}
