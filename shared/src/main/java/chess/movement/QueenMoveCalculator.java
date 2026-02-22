package chess.movement;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * MoveCalculator abstract class for rooks, queens, and bishops.
 * These pieces can move in a single direction as far as they want.
 */
public class QueenMoveCalculator extends KingMoveCalculator {

    public QueenMoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        super(piece, startPosition, board);
    }

    public Collection<ChessMove> calculateMoves() {
        var validMoves = new ArrayList<ChessMove>();

        ChessPosition endPosition;
        ChessPiece pieceAtPosition;

        for (MoveDelta direction : directions) {
            for (int times = 1; times < 8; times++) {
                endPosition = direction.addTo(startPosition, times);

                // Check if move is valid:
                // 1. endPosition is in bounds (if not, skip immediately; don't get piece there)
                if (!endPosition.inBounds()) break;

                // 2. no piece in newPosition, or the piece there is an opposing piece.
                pieceAtPosition = board.getPiece(endPosition);
                if (pieceAtPosition != null && pieceAtPosition.getTeamColor() == piece.getTeamColor())
                    break;
                else {
                    validMoves.add(new ChessMove(startPosition, endPosition, null));
                    if (pieceAtPosition != null) break;
                }
            }
        }

        return validMoves;
    }
}
