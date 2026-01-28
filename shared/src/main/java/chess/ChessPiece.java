package chess;

import chess.movement.*;

import java.util.Collection;
import java.util.Objects;

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
            return switch (this) {
                case KING -> "K";
                case QUEEN -> "Q";
                case BISHOP -> "B";
                case KNIGHT -> "N";
                case ROOK -> "R";
                case PAWN -> "P";
            };
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
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        MoveCalculator moveCalculator = switch(this.type) {
            case KING -> new KingMoveCalculator(this, myPosition, board);
            case KNIGHT -> new KnightMoveCalculator(this, myPosition, board);
            case QUEEN -> new QueenMoveCalculator(this, myPosition, board);
            case BISHOP -> new BishopMoveCalculator(this, myPosition, board);
            case ROOK -> new RookMoveCalculator(this, myPosition, board);
            case PAWN -> new PawnMoveCalculator(this, myPosition, board);
        };

        return moveCalculator.calculateMoves();
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ChessPiece{");
        sb.append("type=").append(type);
        sb.append(", pieceColor=").append(pieceColor);
        sb.append('}');
        return sb.toString();
    }
}
