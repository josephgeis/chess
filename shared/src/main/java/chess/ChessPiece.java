package chess;

import java.util.Collection;
import java.util.Vector;

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
        PAWN
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

        switch(this.type) {
            case KING:
            case QUEEN:
                // Kings can move 1 square in any direction.
                // Borrowing this as a template for queens.
                rowDeltas = new int[]{1, 1, 0, -1, -1, -1, 0, 1};
                colDeltas = new int[]{0, 1, 1,  1,  0, -1, -1, -1};

                // Stop here for kings (only need one in each direction).
                if (this.type == PieceType.KING) break;

                // Queens can go up to 7 in any direction. Use the row/colDeltas made for the king as a basis,
                // then multiply them by up to 7.
                var kingRowDeltas = rowDeltas;
                var kingColDeltas = colDeltas;
                rowDeltas = new int[kingRowDeltas.length * 7];
                colDeltas = new int[kingColDeltas.length * 7];

                for (int multiplier = 1; multiplier < 8; multiplier++) {
                    for (int i = 0; i < kingRowDeltas.length; i++) {
                        rowDeltas[i + kingRowDeltas.length * (multiplier - 1)] = kingRowDeltas[i] * multiplier;
                        colDeltas[i + kingColDeltas.length * (multiplier - 1)] = kingColDeltas[i] * multiplier;
                    }
                }
                break;
            case KNIGHT:
                rowDeltas = new int[]{2, 1, -1, -2, -2, -1, 1, 2};
                colDeltas = new int[]{1, 2, 2, 1, -1, -2, -2, -1};
                break;
            case PAWN:
                rowDeltas = new int[]{1, 2};
                colDeltas = new int[]{0, 0};
                break;
            default:
                throw new RuntimeException("Not implemented");
        }

        ChessPosition newPosition;
        for (int i = 0; i < rowDeltas.length; i++) {
            newPosition = new ChessPosition(myRow + rowDeltas[i], myCol + colDeltas[i]);
            if (newPosition.inBounds()) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        return moves;
    }
}
