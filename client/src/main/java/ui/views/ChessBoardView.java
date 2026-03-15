package ui.views;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.EscapeSequences;
import ui.TerminalController;

public class ChessBoardView extends View {

    private static final TextColor SAND = new TextColor.RGB(163, 147, 130);
    final TextColor NAVY = new TextColor.RGB(0, 46, 93);
    final TextColor ROYAL = new TextColor.RGB(0, 61, 165);
    final TextColor ORANGE = new TextColor.RGB(209, 65, 36);

    final int SQUARE_COLS = 5;
    final int SQUARE_ROWS = 3;
    final int EDGE_THICK_H = 1;
    final int EDGE_THICK_V = 2;

    public ChessBoardView(TextGraphics parentTextGraphics, TerminalController terminalController) {
        super(parentTextGraphics, terminalController);
    }

    @Override
    public void draw() {
        textGraphics.setBackgroundColor(ORANGE);
        textGraphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, textGraphics.getSize(), ' ');

        TerminalPosition startPosition = TerminalPosition.TOP_LEFT_CORNER.withRelative(SQUARE_COLS, SQUARE_ROWS);

        drawBoardEdge(startPosition);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                TerminalPosition position = startPosition.withRelative(5*j, 3*i);
                drawSquare(position, (i + j) % 2 == 0 ? NAVY : ROYAL,
                        new ChessPiece(EscapeSequences.BLACK_PAWN, i < 4));
            }
        }

    }

    private void drawBoardEdge(TerminalPosition startPosition) {
        textGraphics.setBackgroundColor(new TextColor.RGB(209, 204, 189));

        TerminalSize horizontalEdge = new TerminalSize(SQUARE_COLS * 8 + 2 * EDGE_THICK_V, 1);
        TerminalPosition topEdge = startPosition.withRelative( -EDGE_THICK_V, -EDGE_THICK_H);
        textGraphics.fillRectangle(topEdge, horizontalEdge, ' ');
        TerminalPosition bottomEdge = startPosition.withRelative(-EDGE_THICK_V, SQUARE_ROWS * 8);
        textGraphics.fillRectangle(bottomEdge, horizontalEdge, ' ');

        TerminalSize verticalEdge = new TerminalSize(EDGE_THICK_V, SQUARE_ROWS * 8 );
        TerminalPosition leftEdge = startPosition.withRelative(-EDGE_THICK_V, 0);
        textGraphics.fillRectangle(leftEdge, verticalEdge, ' ');
        TerminalPosition rightEdge = startPosition.withRelative(SQUARE_COLS * 8, 0);
        textGraphics.fillRectangle(rightEdge, verticalEdge, ' ');

        drawEdgeAlphaNumber(startPosition);
    }

    private void drawEdgeAlphaNumber(TerminalPosition startPosition) {
        for (int i = 0; i < 8; i++) {
            textGraphics.setForegroundColor(SAND);
            TerminalPosition topLetterPosition = startPosition.withRelative(2 + SQUARE_COLS * i, -1);
            textGraphics.putString(topLetterPosition, "%d".formatted(i + 1));
            TerminalPosition bottomLetterPosition = startPosition.withRelative(2 + SQUARE_COLS * i, 8 * SQUARE_ROWS);
            textGraphics.putString(bottomLetterPosition, "%d".formatted(i + 1));

            TerminalPosition leftNumberPosition = startPosition.withRelative(-1, 1 + SQUARE_ROWS * i);
            textGraphics.putString(leftNumberPosition, "%d".formatted(i + 1));
            TerminalPosition rightNumberPosition = startPosition.withRelative(8 * SQUARE_COLS, 1 + SQUARE_ROWS * i);
            textGraphics.putString(rightNumberPosition, "%d".formatted(i + 1));
        }
    }

    record ChessPiece(String symbol, boolean blackWhite) {
        public TextColor color() {
            return blackWhite ? TextColor.ANSI.WHITE : TextColor.ANSI.BLACK;
        }

        public TextColor textColor() {
            return blackWhite ? TextColor.ANSI.BLACK : TextColor.ANSI.WHITE;
        }
    }

    void drawSquare(TerminalPosition position, TextColor squareColor, ChessPiece piece) {
        textGraphics.setBackgroundColor(squareColor);
        textGraphics.fillRectangle(position,
                new TerminalSize(5, 3), ' ');

        if (piece != null) {
            textGraphics.setBackgroundColor(piece.color());
            textGraphics.setForegroundColor(piece.textColor());
            textGraphics.putString(position.withRelative(1, 1), piece.symbol());
        }
    }
}
