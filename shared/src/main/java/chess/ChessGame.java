package chess;

import chess.piecefinder.ChessPieceFinder;
import chess.piecefinder.ChessPositionPiece;

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
        BLACK
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
        if (piece == null)
            return null;

        return piece.pieceMoves(board, startPosition);
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

        if (moves == null)
            throw new InvalidMoveException("There is no piece at the specified starting location");
        else if (!moves.contains(move))
            throw new InvalidMoveException("The piece at the specified starting location cannot be moved to the specified end location");

        final var piece = board.getPiece(startPosition);

        if (piece.getTeamColor() != currentTeamTurn)
            throw new InvalidMoveException("The piece at the specified starting location cannot be moved out of turn");


        // Make a "test" game to make sure I don't mutate the actual game state.
        var gameClone = this.clone();

        final var finalPosition = move.getEndPosition();
        final var promotionPieceType = move.getPromotionPiece();
        if (promotionPieceType != null) {
            gameClone.board.addPiece(
                    finalPosition,
                    new ChessPiece(piece.getTeamColor(), promotionPieceType)
            );
        } else {
            gameClone.board.addPiece(finalPosition, piece);
        }
        gameClone.board.addPiece(startPosition, null);

        // If it makes the "test" game go into check for this team, it doesn't work.
        // Otherwise, update the actual board.
        if (gameClone.isInCheck(piece.getTeamColor()))
            throw new InvalidMoveException("Cannot move into check.");
        else
            setBoard(gameClone.board);

        setTeamTurn(currentTeamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check.
     *
     * Mechanism: find king of current team, look up pieces of other team and all possible moves,
     * find if there is one piece with one move that could capture the king.
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        final var otherTeam = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;

        var pieceFinder = new ChessPieceFinder(board);
        var king = pieceFinder.findPieces(teamColor, ChessPiece.PieceType.KING).first();
        var otherTeamPieces = pieceFinder.findPieces(otherTeam);

        for (ChessPositionPiece positionPiece : otherTeamPieces) {
            var position = positionPiece.position();
            var piece = positionPiece.piece();

            for (ChessMove move : piece.pieceMoves(board, position)) {
                if (move.getEndPosition().equals(king.position())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;

        var pieceFinder = new ChessPieceFinder(board);
        var teamPieces = pieceFinder.findPieces(teamColor);

        ChessGame gameCopy;

        for (ChessPositionPiece piecePosition : teamPieces) {
            var moves = piecePosition.piece().pieceMoves(board, piecePosition.position());

            for (ChessMove move : moves) {
                gameCopy = this.clone();

                try {
                    gameCopy.makeMove(move);
                } catch (InvalidMoveException ignored) { }

                // Find one instance where there is a move and the game is not in check. That means the game is not in checkmate.
                if (!gameCopy.isInCheck(teamColor)) return false;
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
