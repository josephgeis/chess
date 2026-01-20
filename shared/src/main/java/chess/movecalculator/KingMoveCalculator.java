package chess.movecalculator;

public class KingMoveCalculator extends MoveCalculator {
    public KingMoveCalculator() {
        directions = new MoveVector[] {
                new MoveVector(1, 0),
                new MoveVector(1, 1),
                new MoveVector(0, 1),
                new MoveVector(-1, 1),
                new MoveVector(-1, 0),
                new MoveVector(-1, -1),
                new MoveVector(0, -1),
                new MoveVector(1, -1),
        };
    }
}
