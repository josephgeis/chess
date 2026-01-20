package chess.movecalculator;

import chess.ChessPosition;

public record MoveVector(int deltaRow, int deltaCol) {
    public ChessPosition moveTo(ChessPosition startPosition, int factor) {
        return new ChessPosition(
                startPosition.getRow() + this.deltaRow * factor,
                startPosition.getColumn() + this.deltaCol * factor
        );
    }

    public ChessPosition moveTo(ChessPosition startPosition) {
        return moveTo(startPosition, 1);
    }
}
