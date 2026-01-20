package chess.movecalculator;

public class KnightMoveCalculator extends MoveCalculator {
    public KnightMoveCalculator() {
        directions = new MoveVector[] {
                new MoveVector(2, 1),
                new MoveVector(2, -1),
                new MoveVector(1, 2),
                new MoveVector(1, -2),
                new MoveVector(-2, 1),
                new MoveVector(-2, -1),
                new MoveVector(-1, 2),
                new MoveVector(-1, -2),
        };
    }
}
