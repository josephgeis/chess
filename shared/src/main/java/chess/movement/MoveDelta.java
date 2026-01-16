package chess.movement;

import chess.ChessPosition;

public record MoveDelta(int deltaRow, int deltaCol) {

    /**
     * Takes a ChessPosition and augments it by the current MoveDelta and scale factor.
     * In other words, this calculates the ending position of a piece moving from position multiplied by scaleFactor.
     * @param position The position to move from.
     * @param scaleFactor The scale factor of the move. (i.e., make the move in the same direction this many times.)
     * @return The end position.
     */
    ChessPosition addTo(ChessPosition position, int scaleFactor) {
        return new ChessPosition(
                position.getRow() + (deltaRow * scaleFactor),
                position.getColumn() + (deltaCol * scaleFactor)
        );
    }

    /**
     * Takes a ChessPosition and augments it by the current MoveDelta once.
     * @param position The position to move from.
     * @return The end position.
     */
    ChessPosition addTo(ChessPosition position) {
        return addTo(position, 1);
    }
}
