package chess.movecalculator;

public class RookMoveCalculator extends QueenMoveCalculator {
    public RookMoveCalculator() {
        directions = new MoveVector[] {
                new MoveVector(1, 0),
                new MoveVector(0, 1),
                new MoveVector(-1, 0),
                new MoveVector(0, -1),
        };
    }
}
