package chess;

import chess.piecefinder.ChessPieceFinder;
import chess.piecefinder.ChessPositionPiece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable {

    TeamColor currentTeamTurn;
    ChessBoard board;

    public ChessGame() {
        currentTeamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        public TeamColor otherTeam() {
            return switch (this) {
                case WHITE -> BLACK;
                case BLACK -> WHITE;
            };
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        var pieceMoves = piece.pieceMoves(board, startPosition);
        var validMoves = new ArrayList<ChessMove>();

        for (ChessMove move : pieceMoves) {
            var boardCopy = this.board.clone();
            boardCopy.addPiece(move.getEndPosition(), piece);
            boardCopy.addPiece(move.getStartPosition(), null);

            if (!boardInCheck(boardCopy, piece.getTeamColor())) {
                validMoves.add(move);
            }

        }

        return validMoves;
    }

    /**
     * Helper function to check if the king is in check on a given ChessBoard.
     * @param board The ChessBoard
     * @param teamColor the team color in question
     * @return true if the board is in check for the given team color.
     */
    boolean boardInCheck(ChessBoard board, TeamColor teamColor) {
        var pieceFinder = new ChessPieceFinder(board);
        var king = pieceFinder.findPieces(teamColor, ChessPiece.PieceType.KING).first();
        var enemyPieceLocations = pieceFinder.findPieces(teamColor.otherTeam());

        for (ChessPositionPiece enemyPositionPiece : enemyPieceLocations) {
            final var enemyPiece = enemyPositionPiece.piece();
            final var enemyPosition = enemyPositionPiece.position();

            for (ChessMove enemyMove : enemyPiece.pieceMoves(board, enemyPosition)) {
                if (enemyMove.getEndPosition().equals(king.position())) {
                     return true;
                }
            }
        }

        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        final var startPosition = move.getStartPosition();
        final var moves = validMoves(startPosition);

        if (moves == null) {
            throw new InvalidMoveException("There is no piece at the specified starting location");
        } else if (!moves.contains(move)) {
            throw new InvalidMoveException("The piece at the specified starting location cannot be moved to the specified end location");
        }

        final var piece = board.getPiece(startPosition);

        if (piece.getTeamColor() != currentTeamTurn) {
            throw new InvalidMoveException("The piece at the specified starting location cannot be moved out of turn");
        }

        final var finalPosition = move.getEndPosition();
        final var promotionPieceType = move.getPromotionPiece();
        if (promotionPieceType != null) {
            this.board.addPiece(
                    finalPosition,
                    new ChessPiece(piece.getTeamColor(), promotionPieceType)
            );
        } else {
            this.board.addPiece(finalPosition, piece);
        }
        this.board.addPiece(startPosition, null);

        setTeamTurn(currentTeamTurn.otherTeam());
    }

    /**
     * Determines if the given team is in check.
     * Mechanism: find king of current team, look up pieces of other team and all possible moves,
     * find if there is one piece with one move that could capture the king.
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return boardInCheck(this.board, teamColor);
    }

    boolean teamHasNoMoves(TeamColor teamColor) {
        var pieceFinder = new ChessPieceFinder(this.board);
        var teamPieces = pieceFinder.findPieces(teamColor);

        for (ChessPositionPiece piecePosition : teamPieces) {
            if (!validMoves(piecePosition.position()).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in checkmate (in check + no moves)
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && teamHasNoMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && teamHasNoMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeamTurn == chessGame.currentTeamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamTurn, board);
    }

    @Override
    public ChessGame clone() {
        try {
            ChessGame clone = (ChessGame) super.clone();
            clone.board = board.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
