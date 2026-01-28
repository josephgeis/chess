package chess.piecefinder;

import chess.ChessPiece;
import chess.ChessPosition;

/**
 * Record for mapping a position and piece together
 * @param position
 * @param piece
 */
public record ChessPositionPiece(ChessPosition position, ChessPiece piece) { }
