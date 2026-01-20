package chess.movecalculator;

public class BishopMoveCalculator extends QueenMoveCalculator {
    public BishopMoveCalculator() {
        directions = new MoveVector[] {
                new MoveVector(1, 1),
                new MoveVector(-1, 1),
                new MoveVector(1, -1),
                new MoveVector(-1, -1),
        };
    }
}
