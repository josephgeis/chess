package ui.views;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import ui.menubar.MenuBarItem;
import ui.menubar.MenuItems;

import java.util.EnumSet;

public class ChessBoardView extends View {

    private static final TextColor NAVY = new TextColor.RGB(0, 46, 93);
    private static final TextColor ROYAL = new TextColor.RGB(0, 61, 165);
    private static final TextColor ORANGE = new TextColor.RGB(209, 65, 36);
    private static final TextColor EDGE_COLOR = new TextColor.RGB(209, 204, 189);
    private static final TextColor LABEL_COLOR = new TextColor.RGB(163, 147, 130);

    final int SQUARE_COLS = 5;
    final int SQUARE_ROWS = 3;
    final int EDGE_THICK_H = 1;
    final int EDGE_THICK_V = 2;

    Runnable unwind;
    ChessGame chessGame;
    ChessGame.TeamColor myTeam;

    public ChessBoardView(TextGraphics parentTextGraphics, Runnable unwind) {
        super(parentTextGraphics);
        this.unwind = unwind;

        menuItems = new MenuItems() {
            @Override
            protected MenuBarItem itemAt(int i) {
                return switch (i) {
                    case 5 -> MenuBarItem.withCallback("Switch", () -> myTeam = myTeam.otherTeam());
                    case 6 -> MenuBarItem.withCallback("Close", unwind);
                    default -> null;
                };
            }
        };

        chessGame = new ChessGame();
        myTeam = ChessGame.TeamColor.WHITE;
    }

    record ScreenPosition(int row, int col) {
        public ChessPosition toChessPosition(ChessGame.TeamColor perspective) {
            return switch (perspective) {
                case WHITE -> new ChessPosition(8 - row, col + 1);
                case BLACK -> new ChessPosition(row + 1, 8 - col);
            };
        }
    }

    @Override
    public void draw() {
        textGraphics.setBackgroundColor(ORANGE);
        textGraphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, textGraphics.getSize(), ' ');

        TerminalPosition startPosition = TerminalPosition.TOP_LEFT_CORNER.withRelative(SQUARE_COLS, SQUARE_ROWS);

        drawBoardEdge(startPosition);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ScreenPosition screenPosition = new ScreenPosition(i, j);

                TerminalPosition position = startPosition.withRelative(5*j, 3*i);
                drawSquare(position, (i + j) % 2 == 0 ? ROYAL : NAVY,
                        chessGame.getBoard().getPiece(screenPosition.toChessPosition(myTeam))
                );
            }
        }
    }

    private void drawBoardEdge(TerminalPosition startPosition) {
        textGraphics.setBackgroundColor(EDGE_COLOR);

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
        final Character[] LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        for (int i = 0; i < 8; i++) {
            ScreenPosition screenPosition = new ScreenPosition(i, i);
            ChessPosition chessPosition = screenPosition.toChessPosition(myTeam);

            textGraphics.setForegroundColor(LABEL_COLOR);
            textGraphics.setModifiers(EnumSet.of(SGR.BOLD));
            TerminalPosition topLetterPosition = startPosition.withRelative(2 + SQUARE_COLS * i, -1);
            textGraphics.putString(topLetterPosition, "%c".formatted(LETTERS[chessPosition.getColumn() - 1]));
            TerminalPosition bottomLetterPosition = startPosition.withRelative(2 + SQUARE_COLS * i, 8 * SQUARE_ROWS);
            textGraphics.putString(bottomLetterPosition, "%c".formatted(LETTERS[chessPosition.getColumn() - 1]));

            TerminalPosition leftNumberPosition = startPosition.withRelative(-1, 1 + SQUARE_ROWS * i);
            textGraphics.putString(leftNumberPosition, "%d".formatted(chessPosition.getRow()));
            TerminalPosition rightNumberPosition = startPosition.withRelative(8 * SQUARE_COLS, 1 + SQUARE_ROWS * i);
            textGraphics.putString(rightNumberPosition, "%d".formatted(chessPosition.getRow()));
        }
    }

    void drawSquare(TerminalPosition position, TextColor squareColor, ChessPiece piece) {
        textGraphics.setBackgroundColor(squareColor);
        textGraphics.fillRectangle(position,
                new TerminalSize(5, 3), ' ');

        if (piece != null) {
            textGraphics.setBackgroundColor(colorFor(piece.getTeamColor()));
            textGraphics.setForegroundColor(colorFor(piece.getTeamColor().otherTeam()));
            textGraphics.putString(position.withRelative(1, 1),
                    " %s ".formatted(piece.getPieceType()));
        }
    }

    TextColor colorFor(ChessGame.TeamColor teamColor) {
        return switch (teamColor) {
            case WHITE -> TextColor.ANSI.WHITE_BRIGHT;
            case BLACK -> TextColor.ANSI.BLACK;
        };
    }
}
