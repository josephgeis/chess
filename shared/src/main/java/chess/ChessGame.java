package chess;

import chess.piecefinder.ChessPieceFinder;
import chess.piecefinder.ChessPositionPiece;

import java.util.*;

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

    public Collection<ChessMove> castlingMoves(ChessPosition startPosition) {
        var castlingMoves = new ArrayList<ChessMove>();
        var piece = board.getPiece(startPosition);

        if (piece == null || piece.getPieceType() != ChessPiece.PieceType.KING || piece.getMoves() > 0) return null;

        final var kingRow = startPosition.getRow();
        final var queenRook = board.getPiece(new ChessPosition(kingRow, 1));
        final var kingRook = board.getPiece(new ChessPosition(kingRow, 8));


        if (queenRook != null && queenRook.getMoves() == 0) {
            try {
                assert board.getPiece(new ChessPosition(kingRow, 2)) == null;
                assert board.getPiece(new ChessPosition(kingRow, 3)) == null;
                assert board.getPiece(new ChessPosition(kingRow, 4)) == null;
                castlingMoves.add(new ChessMove(startPosition, new ChessPosition(kingRow, 3), null));
            } catch (AssertionError ignored) { }
        }

        if (kingRook != null && kingRook.getMoves() == 0) {
            try {
                assert board.getPiece(new ChessPosition(kingRow, 6)) == null;
                assert board.getPiece(new ChessPosition(kingRow, 7)) == null;
                castlingMoves.add(new ChessMove(startPosition, new ChessPosition(kingRow, 7), null));
            } catch (AssertionError ignored) { }
        }

        return castlingMoves;
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

        final var enemyTeam = piece.getTeamColor().otherTeam();

        var pieceMoves = piece.pieceMoves(board, startPosition);

        var castlingMoves = this.castlingMoves(startPosition);
        if (castlingMoves != null) pieceMoves.addAll(castlingMoves);

        var validMoves = new ArrayList<ChessMove>();

        for (ChessMove move : pieceMoves) {
            var boardCopy = this.board.clone();
            boardCopy.addPiece(move.getEndPosition(), piece);
            boardCopy.addPiece(move.getStartPosition(), null);

            if (castlingMoves != null && castlingMoves.contains(move)) {
                var rookColumn = move.getEndPosition().getColumn() == 3 ? 1 : 8;
                var rookNewColumn = rookColumn == 1 ? 4 : 6;
                var rookOldPosition = new ChessPosition(move.getEndPosition().getRow(), rookColumn);
                var rook = boardCopy.getPiece(rookOldPosition);

                boardCopy.addPiece(new ChessPosition(move.getEndPosition().getRow(), rookNewColumn), rook);
                boardCopy.addPiece(rookOldPosition, null);
            }

            var pieceFinder = new ChessPieceFinder(boardCopy);
            var king = pieceFinder.findPieces(piece.getTeamColor(), ChessPiece.PieceType.KING).first();
            var enemyPieceLocations = pieceFinder.findPieces(enemyTeam);

            try {
                for (ChessPositionPiece enemyPositionPiece : enemyPieceLocations) {
                    final var enemyPiece = enemyPositionPiece.piece();
                    final var enemyPosition = enemyPositionPiece.position();

                    for (ChessMove enemyMove : enemyPiece.pieceMoves(boardCopy, enemyPosition)) {
                        if (enemyMove.getEndPosition().equals(king.position())) {
                            throw new InvalidMoveException("This move will check the king.");
                        }
                    }
                }

                validMoves.add(move);

            } catch (InvalidMoveException ignored) {  }

        }

        return validMoves;
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

        final var castlingMoves = castlingMoves(startPosition);
        final var castleMove = castlingMoves != null && castlingMoves.contains(move);

        final var finalPosition = move.getEndPosition();
        final var promotionPieceType = move.getPromotionPiece();
        piece.incrementMoves();
        if (promotionPieceType != null) {
            this.board.addPiece(
                    finalPosition,
                    new ChessPiece(piece.getTeamColor(), promotionPieceType, piece.getMoves())
            );
        } else {
            this.board.addPiece(finalPosition, piece);
        }
        this.board.addPiece(startPosition, null);

        if (castleMove) {
            var rookColumn = move.getEndPosition().getColumn() == 3 ? 1 : 8;
            var rookNewColumn = rookColumn == 1 ? 4 : 6;
            var rookOldPosition = new ChessPosition(move.getEndPosition().getRow(), rookColumn);
            var rook = board.getPiece(rookOldPosition);

            this.board.addPiece(new ChessPosition(move.getEndPosition().getRow(), rookNewColumn), rook);
            this.board.addPiece(rookOldPosition, null);

            rook.incrementMoves();
        }

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
        final var otherTeam = teamColor.otherTeam();

        var pieceFinder = new ChessPieceFinder(board);
        var king = pieceFinder.findPieces(teamColor, ChessPiece.PieceType.KING).first();
        var otherTeamPieces = pieceFinder.findPieces(otherTeam);

        for (ChessPositionPiece enemyPositionPiece : otherTeamPieces) {
            for (ChessMove move : validMoves(enemyPositionPiece.position())) {
                if (move.getEndPosition().equals(king.position())) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean teamHasNoMoves(TeamColor teamColor) {
        var pieceFinder = new ChessPieceFinder(this.board);
        var teamPieces = pieceFinder.findPieces(teamColor);

        for (ChessPositionPiece piecePosition : teamPieces) {
            if (!validMoves(piecePosition.position()).isEmpty())
                return false;
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
