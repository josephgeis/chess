package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] pieces;

    public ChessBoard() {
        pieces = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        final var row = position.getRow();
        final var col = position.getColumn();

        pieces[8 - row][col - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        final var row = position.getRow();
        final var col = position.getColumn();

        return pieces[8 - row][col - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     *
     * Black is on top, white on bottom.
     */
    public void resetBoard() {
        // Add the pawns in (that's the easy step.)
        for (int col = 1; col <= 8; col++) {
            addPiece(
                    new ChessPosition(7, col),
                    new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)
            );

            addPiece(
                    new ChessPosition(2, col),
                    new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)
            );
        }

        for (int row = 1; row <= 8; row += 7) {
            final var teamColor = row == 1 ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            for (int col = 1; col <= 8; col++) {
                final ChessPiece.PieceType pieceType = switch (col) {
                    case 1, 8 -> ChessPiece.PieceType.ROOK;
                    case 2, 7 -> ChessPiece.PieceType.KNIGHT;
                    case 3, 6 -> ChessPiece.PieceType.BISHOP;
                    case 4 -> ChessPiece.PieceType.QUEEN;
                    case 5 -> ChessPiece.PieceType.KING;
                    default -> throw new IndexOutOfBoundsException("Exceeded the width of the board.");
                };

                addPiece(
                        new ChessPosition(row, col),
                        new ChessPiece(teamColor, pieceType)
                );
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(pieces, that.pieces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(pieces);
    }

    @Override
    public String toString() {
        var string = new StringBuilder();

        ChessPiece piece;
        ChessPiece.PieceType pieceType;

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                piece = getPiece(new ChessPosition(r, c));
                if (piece != null) {
                    pieceType = piece.getPieceType();

                    string.append("|").append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                            pieceType.toString().toUpperCase() : pieceType.toString().toLowerCase());
                } else {
                    string.append("| ");
                }
            }

            string.append("|\n");
        }

        return string.toString();
    }
}
