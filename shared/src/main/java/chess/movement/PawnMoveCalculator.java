package chess.movement;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator extends MoveCalculator {

    public PawnMoveCalculator(ChessPiece piece, ChessPosition startPosition, ChessBoard board) {
        super(piece, startPosition, board);
    }

    static ChessPiece.PieceType[] promotionTypes = {
            ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.QUEEN
    };

    public Collection<ChessMove> doPromotions(ChessMove move) {
        var moves = new ArrayList<ChessMove>(4);

        for (ChessPiece.PieceType pieceType : promotionTypes) {
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), pieceType));
        }

        return moves;
    }

    @Override
    public Collection<ChessMove> calculateMoves() {
        var moves = new ArrayList<ChessMove>();

        int firstRow, lastRow, directionFactor;

        switch (piece.getTeamColor()) {
            case WHITE:
                firstRow = 2;
                lastRow = 8;
                directionFactor = 1;
                break;
            case BLACK:
                firstRow = 7;
                lastRow = 1;
                directionFactor = -1;
                break;
            default: throw new RuntimeException("Error in the PawnMoveCalculator");
        }

        var advanceDelta = new MoveDelta(1, 0);

        var singleAdvancePosition = advanceDelta.addTo(startPosition, directionFactor);
        var doubleAdvancePosition = advanceDelta.addTo(startPosition, 2 * directionFactor);

        if (singleAdvancePosition.inBounds()) {
            var otherPiece = board.getPiece(singleAdvancePosition);

            if (otherPiece == null) {
                var move = new ChessMove(startPosition, singleAdvancePosition, null);

                if (singleAdvancePosition.getRow() == lastRow)
                    moves.addAll(doPromotions(move));
                else {
                    moves.add(move);

                    if (startPosition.getRow() == firstRow) {
                        otherPiece = board.getPiece(doubleAdvancePosition);

                        if (otherPiece == null)
                            moves.add(new ChessMove(startPosition, doubleAdvancePosition, null));
                    }
                }
            }
        }

        final var captureLeftDelta = new MoveDelta(1, -1);
        final var captureRightDelta = new MoveDelta(1, 1);

        final var captureDeltas = new MoveDelta[] {
                captureLeftDelta,
                captureRightDelta
        };

        for (MoveDelta captureDelta : captureDeltas) {
            var capturePosition = captureDelta.addTo(startPosition, directionFactor);

            if (capturePosition.inBounds()) {
                var otherPiece = board.getPiece(capturePosition);

                if (otherPiece != null && otherPiece.getTeamColor() != piece.getTeamColor()) {
                    var move = new ChessMove(startPosition, capturePosition, null);
                    if (capturePosition.getRow() == lastRow) {
                        moves.addAll(doPromotions(move));
                    } else {
                        moves.add(move);
                    }
                }
            }
        }

        return moves;
    }
}
