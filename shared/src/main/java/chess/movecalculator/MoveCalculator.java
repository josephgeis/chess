package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.Vector;

public abstract class MoveCalculator {
    MoveVector [] directions;

    public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition startPosition) {
        var moves = new Vector<ChessMove>();

        ChessMove newMove;
        ChessPosition endPosition;
        ChessPiece pieceAtNewLocation;
        int row, col;

        for (MoveVector direction : directions) {
            endPosition = direction.applyTo(startPosition);
            row = endPosition.getRow();
            col = endPosition.getColumn();

            // Skip if new position out of bounds
            if (!(1 <= row && row <= 8 && 1 <= col && col <= 8))
                continue;

            pieceAtNewLocation = board.getPiece(endPosition);

            if (pieceAtNewLocation != null && pieceAtNewLocation.getTeamColor() == piece.getTeamColor())
                continue;

            moves.add(new ChessMove(startPosition, endPosition, null));
        }

        return moves;
    }
}
