package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.Vector;

/**
 * Special container for row/column deltas. See ChessPiece.calculateFullBoardMoveDeltas
 * @param rowDeltas
 * @param colDeltas
 */
record RowColumnDeltas(int[] rowDeltas, int[] colDeltas) {}

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN;

        @Override
        public String toString() {
            switch (this) {
                case KING: return "K";
                case QUEEN: return "Q";
                case BISHOP: return "B";
                case KNIGHT: return "N";
                case ROOK: return "R";
                case PAWN: return "P";
                default: throw new RuntimeException("PieceType toString failed.");
            }
        }
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Helper method for calculating potential moves for queens, rooks, and bishops
     * since they can move up to 7 spaces.
     * @param origRowDeltas row deltas to dilate
     * @param origColDeltas col deltas to dilate
     * @return a special pair record containing the modified row/column deltas
     */
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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // This will ultimately be returned
        var moves = new Vector<ChessMove>();

        // get current row and column
        final var myRow = myPosition.getRow();
        final var myCol = myPosition.getColumn();

        //
        int[] rowDeltas, colDeltas;
        RowColumnDeltas rowColDeltas;

        switch(this.type) {
            case KING:
            case QUEEN:
                // Kings can move 1 square in any direction.
                // Borrowing this as a template for queens.
                // FIXME: I forgot queens can't jump. Not as straightforward.
                rowDeltas = new int[]{1, 1, 0, -1, -1, -1, 0, 1};
                colDeltas = new int[]{0, 1, 1,  1,  0, -1, -1, -1};

                // Stop here for kings (only need one in each direction).
                if (this.type == PieceType.KING) break;

                // Queens can go up to 7 in any direction. Use the row/colDeltas made for the king as a basis,
                // then multiply them by up to 7.
                rowColDeltas = calculateFullBoardMoveDeltas(rowDeltas, colDeltas);
                rowDeltas = rowColDeltas.rowDeltas();
                colDeltas = rowColDeltas.colDeltas();
                break;
            case KNIGHT:
                rowDeltas = new int[]{2, 1, -1, -2, -2, -1, 1, 2};
                colDeltas = new int[]{1, 2, 2, 1, -1, -2, -2, -1};
                break;
            case PAWN:
                // FIXME: implement special capturing rules
                rowDeltas = new int[]{1, 2};
                colDeltas = new int[]{0, 0};
                break;
            case ROOK:
                // Rooks can move in one direction up to 7 spaces.
                rowDeltas = new int[]{1, 0, -1, 0};
                colDeltas = new int[]{0, 1, 0, -1};

                rowColDeltas = calculateFullBoardMoveDeltas(rowDeltas, colDeltas);
                rowDeltas = rowColDeltas.rowDeltas();
                colDeltas = rowColDeltas.colDeltas();
                break;
            case BISHOP:
                // Bishops can diagonally up to 7 spaces.
                rowDeltas = new int[]{1, -1, -1, 1};
                colDeltas = new int[]{1, 1, -1, -1};

                rowColDeltas = calculateFullBoardMoveDeltas(rowDeltas, colDeltas);
                rowDeltas = rowColDeltas.rowDeltas();
                colDeltas = rowColDeltas.colDeltas();
                break;
            default:
                throw new RuntimeException("Not implemented");
        }

        ChessPosition newPosition;
        ChessPiece pieceAtPosition;

        for (int i = 0; i < rowDeltas.length; i++) {
            newPosition = new ChessPosition(myRow + rowDeltas[i], myCol + colDeltas[i]);
            // Check if move is valid:
            // 1. newPosition is in bounds (if not, skip immediately; don't get piece there)
            if (!newPosition.inBounds()) continue;

            // 2. no piece in newPosition, or the piece there is an opposing piece.
            pieceAtPosition = board.getPiece(newPosition);
            if (pieceAtPosition == null || pieceAtPosition.pieceColor != this.pieceColor)
                moves.add(new ChessMove(myPosition, newPosition, null));
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
