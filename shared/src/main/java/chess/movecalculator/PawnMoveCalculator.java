package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import static chess.ChessPiece.PieceType.*;

public class PawnMoveCalculator extends MoveCalculator {

    static Collection<ChessMove> doPromotions(ChessMove chessMove) {
        final var pieces = new ChessPiece.PieceType[] {
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT
        };

        var promotedMoves = new Vector<ChessMove>(4);

        for (ChessPiece.PieceType piece : pieces) {
            promotedMoves.add(new ChessMove(chessMove.getStartPosition(), chessMove.getEndPosition(), piece));
        }

        return promotedMoves;
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition startPosition) {
        var moves = new Vector<ChessMove>();

        var rowFactor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        final var firstRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7;
        final var lastRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;

        MoveVector singleAdvanceVector = new MoveVector(rowFactor, 0);
        MoveVector doubleAdvanceVector = new MoveVector(2 * rowFactor, 0);
        MoveVector captureLeftVector = new MoveVector(rowFactor, -1);
        MoveVector captureRightVector = new MoveVector(rowFactor, 1);

        ChessPosition singleAdvancePosition = singleAdvanceVector.applyTo(startPosition);
        ChessPosition doubleAdvancePosition = doubleAdvanceVector.applyTo(startPosition);
        ChessPosition captureLeftPosition = captureLeftVector.applyTo(startPosition);
        ChessPosition captureRightPosition = captureRightVector.applyTo(startPosition);

        ChessMove newMove;

        if (singleAdvancePosition.isInBounds() && board.getPiece(singleAdvancePosition) == null) {
            newMove = new ChessMove(startPosition, singleAdvancePosition, null);
            if (singleAdvancePosition.getRow() == lastRow) {
                moves.addAll(doPromotions(newMove));
            } else {
                moves.add(newMove);

                if (startPosition.getRow() == firstRow && doubleAdvancePosition.isInBounds() && board.getPiece(doubleAdvancePosition) == null)
                    moves.add(new ChessMove(startPosition, doubleAdvancePosition, null));
            }
        }

        if (captureLeftPosition.isInBounds()) {
            ChessPiece captureLeftPiece = board.getPiece(captureLeftPosition);
            if (captureLeftPiece != null && captureLeftPiece.getTeamColor() != piece.getTeamColor()) {
                newMove = new ChessMove(startPosition, captureLeftPosition, null);
                if (captureLeftPosition.getRow() == lastRow) {
                    moves.addAll(doPromotions(newMove));
                } else {
                    moves.add(newMove);
                }
            }
            // run promotions
        }

        if (captureRightPosition.isInBounds()) {
            ChessPiece captureRightPiece = board.getPiece(captureRightPosition);
            if (captureRightPiece != null && captureRightPiece.getTeamColor() != piece.getTeamColor()) {
                newMove = new ChessMove(startPosition, captureRightPosition, null);
                if (captureRightPosition.getRow() == lastRow) {
                    moves.addAll(doPromotions(newMove));
                } else {
                    moves.add(newMove);
                }
            }
            // run promotions
        }

        return moves;
    }
}
