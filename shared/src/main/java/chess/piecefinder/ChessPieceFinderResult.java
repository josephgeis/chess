package chess.piecefinder;

import java.util.Iterator;
import java.util.Set;

public class ChessPieceFinderResult implements Iterable<ChessPositionPiece> {
    Set<ChessPositionPiece> positionPieceSet;

    protected ChessPieceFinderResult(Set<ChessPositionPiece> positionPieceSet) {
        this.positionPieceSet = positionPieceSet;
    }

    public ChessPositionPiece first() {
        if (positionPieceSet.isEmpty()) return null;

        return iterator().next();
    }

    @Override
    public Iterator<ChessPositionPiece> iterator() {
        return positionPieceSet.iterator();
    }
}
