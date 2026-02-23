package chess.piecefinder;

import chess.*;

import java.util.HashSet;

/**
 * Class for finding pieces by team color and optionally piece type.
 */
public class ChessPieceFinder {
    ChessBoard board;

    public ChessPieceFinder(ChessBoard board) {
        this.board = board;
    }

    /**
     * Get the pieces for a certain team mapped by their position.
     *
     * @param teamColor Team color to look up for.
     * @return Map containing pieces for a certain team with their corresponding position.
     */
    public ChessPieceFinderResult findPieces(ChessGame.TeamColor teamColor) {
        var pieces = new HashSet<ChessPositionPiece>();

        ChessPosition position;
        ChessPiece piece;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                position = new ChessPosition(row, col);
                piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    pieces.add(new ChessPositionPiece(position, piece));
                }
            }
        }

        return new ChessPieceFinderResult(pieces);
    }

    /**
     * Finds all pieces on the board for a given team and type.
     *
     * @param teamColor Color of interest
     * @param pieceType Piece type of interest
     * @return Set containing the matching pieces
     */
    public ChessPieceFinderResult findPieces(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType) {
        var pieces = new HashSet<ChessPositionPiece>();
        final var teamColorPieces = findPieces(teamColor);

        for (ChessPositionPiece result : teamColorPieces) {
            if (result.piece().getPieceType() == pieceType) {
                pieces.add(result);
            }
        }

        return new ChessPieceFinderResult(pieces);
    }
}
