package chess.movecalculator;

import chess.ChessPosition;

public record MoveVector(int deltaRow, int deltaCol) {
    public ChessPosition applyTo(ChessPosition startPosition, int factor) {
        return new ChessPosition(
                startPosition.getRow() + this.deltaRow * factor,
                startPosition.getColumn() + this.deltaCol * factor
        );
    }

    public ChessPosition applyTo(ChessPosition startPosition) {
        return applyTo(startPosition, 1);
    }
}
