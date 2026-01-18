package chess;

import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece [][] chessPieces;

    public ChessBoard() {
        this.chessPieces = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessPieces[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessPieces[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        for (int row = 1; row <= 8; row += 7) {
            TeamColor color = row == 1 ? TeamColor.WHITE : TeamColor.BLACK;
            int pawnRow = row == 1 ? 2 : 7;
            addPiece(new ChessPosition(row, 1), new ChessPiece(color, PieceType.ROOK));
            addPiece(new ChessPosition(row, 2), new ChessPiece(color, PieceType.KNIGHT));
            addPiece(new ChessPosition(row, 3), new ChessPiece(color, PieceType.BISHOP));
            addPiece(new ChessPosition(row, 4), new ChessPiece(color, PieceType.QUEEN));
            addPiece(new ChessPosition(row, 5), new ChessPiece(color, PieceType.KING));
            addPiece(new ChessPosition(row, 6), new ChessPiece(color, PieceType.BISHOP));
            addPiece(new ChessPosition(row, 7), new ChessPiece(color, PieceType.KNIGHT));
            addPiece(new ChessPosition(row, 8), new ChessPiece(color, PieceType.ROOK));

            for (int col = 1; col <= 8; col++)
                addPiece(new ChessPosition(pawnRow, col), new ChessPiece(color, PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(chessPieces, that.chessPieces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessPieces);
    }
}
