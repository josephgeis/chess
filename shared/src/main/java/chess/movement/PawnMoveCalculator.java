package chess.movement;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.Vector;
import java.util.function.Predicate;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

public class PawnMoveCalculator extends MoveCalculator {
    private static final ChessPiece.PieceType[] promotionPieces = { QUEEN, KNIGHT, BISHOP, ROOK };

    public PawnMoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        super(piece, startPosition, board);
    }

    private void addPromotionMoves(ChessPosition endPosition, Collection<ChessMove> moves) {
        for (ChessPiece.PieceType promotionPiece : promotionPieces)
            moves.add(new ChessMove(startPosition, endPosition, promotionPiece));
    }

    @Override
    public Collection<ChessMove> calculateMoves() {
        var moves = new Vector<ChessMove>();
        var teamColor = piece.getTeamColor();
        var startRow = startPosition.getRow();
        var isFirstMove = (teamColor == WHITE && startRow == 2) || (teamColor == BLACK && startRow == 7);
        final Predicate<Integer> willPromote = row -> row == switch (teamColor) {
            case WHITE -> 8;
            case BLACK -> 1;
        };

        var advanceSingleDelta = switch (teamColor) {
            case WHITE -> new MoveDelta(1, 0);
            case BLACK -> new MoveDelta(-1, 0);
        };
        var advanceSinglePosition = advanceSingleDelta.addTo(startPosition);

        var advanceDoubleDelta = switch (teamColor) {
            case WHITE -> new MoveDelta(2, 0);
            case BLACK -> new MoveDelta(-2, 0);
        };
        var advanceDoublePosition = advanceDoubleDelta.addTo(startPosition);

        var captureLeftDelta = switch (teamColor) {
            case WHITE -> new MoveDelta(1, -1);
            case BLACK -> new MoveDelta(-1, -1);
        };
        var captureLeftPosition = captureLeftDelta.addTo(startPosition);

        var captureRightDelta = switch(teamColor) {
            case WHITE -> new MoveDelta(1, 1);
            case BLACK -> new MoveDelta(-1, 1);
        };
        var captureRightPosition = captureRightDelta.addTo(startPosition);

        if (advanceSinglePosition.inBounds() && board.getPiece(advanceSinglePosition) == null) {
            if (willPromote.test(advanceSinglePosition.getRow()))
                addPromotionMoves(advanceSinglePosition, moves);
            else
                moves.add(new ChessMove(startPosition, advanceSinglePosition, null));

            if (isFirstMove && advanceDoublePosition.inBounds() && board.getPiece(advanceDoublePosition) == null) {
                moves.add(new ChessMove(startPosition, advanceDoublePosition, null));
            }
        }

        ChessPiece otherPiece;

        if (captureLeftPosition.inBounds()) {
            otherPiece = board.getPiece(captureLeftPosition);
            if (otherPiece != null && otherPiece.getTeamColor() != teamColor) {
                if (willPromote.test(captureLeftPosition.getRow()))
                    addPromotionMoves(captureLeftPosition, moves);
                else
                    moves.add(new ChessMove(startPosition, captureLeftPosition, null));
            }
        }

        if (captureRightPosition.inBounds()) {
            otherPiece = board.getPiece(captureRightPosition);
            if (otherPiece != null && otherPiece.getTeamColor() != teamColor) {
                if (willPromote.test(captureRightPosition.getRow()))
                    addPromotionMoves(captureRightPosition, moves);
                else
                    moves.add(new ChessMove(startPosition, captureRightPosition, null));
            }
        }

        return moves;
    }
}
