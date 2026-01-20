package chess.movecalculator;

import chess.*;

import java.util.Collection;
import java.util.Vector;

public class PawnMoveCalculator extends MoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition startPosition) {
        var moves = new Vector<ChessMove>();

        var rowFactor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        final var firstRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 8;
        final var lastRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;

        MoveVector singleAdvanceVector = new MoveVector(rowFactor, 0);
        MoveVector doubleAdvanceVector = new MoveVector(2 * rowFactor, 0);
        MoveVector captureLeftVector = new MoveVector(rowFactor, -1);
        MoveVector captureRightVector = new MoveVector(rowFactor, 1);

        ChessPosition singleAdvancePosition = singleAdvanceVector.applyTo(startPosition);
        ChessPosition doubleAdvancePosition = doubleAdvanceVector.applyTo(startPosition);
        ChessPosition captureLeftPosition = captureLeftVector.applyTo(startPosition);
        ChessPosition captureRightPosition = captureRightVector.applyTo(startPosition);

        if (singleAdvancePosition.isInBounds() && board.getPiece(singleAdvancePosition) == null)
//            if (singleAdvancePosition.getRow() == lastRow) {
                // Promotion
//            } else {
                moves.add(new ChessMove(startPosition, singleAdvancePosition, null));
//            }

        if (startPosition.getRow() == firstRow && doubleAdvancePosition.isInBounds() && board.getPiece(doubleAdvancePosition) == null)
            moves.add(new ChessMove(startPosition, doubleAdvancePosition, null));

        if (captureLeftPosition.isInBounds()) {
            ChessPiece captureLeftPiece = board.getPiece(captureLeftPosition);
            if (captureLeftPiece != null && captureLeftPiece.getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(startPosition, captureLeftPosition, null));
            }
            // run promotions
        }

        if (captureRightPosition.isInBounds()) {
            ChessPiece captureRightPiece = board.getPiece(captureRightPosition);
            if (captureRightPiece != null && captureRightPiece.getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(startPosition, captureRightPosition, null));
            }
            // run promotions
        }

        return moves;
    }
}
