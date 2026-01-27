package chess.movement;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.Vector;

/**
 * MoveCalculator abstract class for rooks, queens, and bishops.
 * These pieces can move in a single direction as far as they want.
 */
public class QueenMoveCalculator extends KingMoveCalculator {

    public QueenMoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        super(piece, startPosition, board);
    }

    public Collection<ChessMove> calculateMoves() {
        var validMoves = new Vector<ChessMove>();

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

/*

\/**
     * Helper method for calculating potential moves for queens, rooks, and bishops
     * since they can move up to 7 spaces.
     * @param origRowDeltas row deltas to dilate
     * @param origColDeltas col deltas to dilate
     * @return a special pair record containing the modified row/column deltas
     *\/
private static RowColumnDeltas calculateFullBoardMoveDeltas(int[] origRowDeltas, int[] origColDeltas) {
    assert origRowDeltas.length == origColDeltas.length;

    var rowDeltas = new int[origRowDeltas.length * 7];
    var colDeltas = new int[origColDeltas.length * 7];

    for (int multiplier = 1; multiplier < 8; multiplier++) {
        for (int i = 0; i < origRowDeltas.length; i++) {
            rowDeltas[i + origRowDeltas.length * (multiplier - 1)] = origRowDeltas[i] * multiplier;
            colDeltas[i + origColDeltas.length * (multiplier - 1)] = origColDeltas[i] * multiplier;
        }
    }

    return new RowColumnDeltas(rowDeltas, colDeltas);
}

 */
