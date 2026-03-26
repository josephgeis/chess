package ui.views;

import chess.*;
import client.MessageObserver;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import com.googlecode.lanterna.input.KeyStroke;
import model.GameData;
import ui.menubar.MenuBarItem;
import ui.menubar.MenuItems;
import websocket.messages.*;

import java.util.*;

public class ChessBoardView extends View implements MessageObserver {

    private static final TextColor NAVY = new TextColor.RGB(0, 46, 93);
    private static final TextColor ROYAL = new TextColor.RGB(0, 61, 165);
    private static final TextColor ORANGE = new TextColor.RGB(209, 65, 36);
    private static final TextColor EDGE_COLOR = new TextColor.RGB(209, 204, 189);
    private static final TextColor LABEL_COLOR = new TextColor.RGB(163, 147, 130);

    final int SQUARE_COLS = 5;
    final int SQUARE_ROWS = 3;
    final int EDGE_THICK_H = 1;
    final int EDGE_THICK_V = 2;

    final int BOARD_WIDTH = (EDGE_THICK_V * 2) + 8 * SQUARE_COLS;

    boolean showHelp = false;
    String[] fnKeys = {"F1", "F2", "F3", "F4", "F5", "F6"};
    String[] helpStrings = {
            "Select a piece to show and select moves on",
            "Submit the selected move",
            "Clear the piece cursor and redraw the board",
            "Resigns the game",
            "Toggle this message",
            "Leave the game"
    };

    List<PresentableMessage> notifications;

    Runnable unwind;
    ChessGame chessGame;
    ChessGame.TeamColor myTeam;

    enum CursorMode {
        DISABLED,
        START,
        END
    }

    CursorMode cursorMode = CursorMode.DISABLED;
    ScreenPosition startPositionCursor;
    ScreenPosition endPositionCursor;

    public ChessBoardView(TextGraphics parentTextGraphics, Runnable unwind) {
        this(parentTextGraphics, null, unwind);
    }

    public ChessBoardView(TextGraphics parentTextGraphics, ChessGame.TeamColor teamColor, Runnable unwind) {
        super(parentTextGraphics);
        this.unwind = unwind;

        menuItems = new MenuItems() {
            @Override
            protected MenuBarItem itemAt(int i) {
                return switch (i) {
                    case 1 -> teamColor != null ? MenuBarItem.withCallback("ShowMove", ChessBoardView.this::showLegalMoves) : null;
                    case 2 -> teamColor != null ? MenuBarItem.withCallback("SubmitMv", ChessBoardView.this::onSubmitMove) : null;
                    case 3 -> MenuBarItem.withCallback("Reload", ChessBoardView.this::onReload);
                    case 4 -> teamColor != null ? MenuBarItem.withCallback("Resign", ChessBoardView.this::onResign) : null;
                    case 5 -> MenuBarItem.withCallback("Help", ChessBoardView.this::toggleHelpScreen);
                    case 6 -> MenuBarItem.withCallback("Leave", ChessBoardView.this::onLeave);
                    default -> null;
                };
            }
        };

        chessGame = new ChessGame();
        chessGame.setBoard(new ChessBoard());

        myTeam = teamColor;
        notifications = new LinkedList<>();
    }

    record ScreenPosition(int row, int col) {
        public ChessPosition toChessPosition(ChessGame.TeamColor perspective) {
            if (perspective == ChessGame.TeamColor.BLACK) {
                return new ChessPosition(row + 1, 8 - col);
            } else {
                return new ChessPosition(8 - row, col + 1);
            }
        }

        public String toAlgNot(ChessGame.TeamColor perspective) {
            ChessPosition chessPosition = this.toChessPosition(perspective);
            Character column = (char) ('a' + chessPosition.getColumn() - 1);
            return "%c%d".formatted(column, chessPosition.getRow());
        }

        public ScreenPosition withRelative(int row, int col) {
            int newRow = Integer.max(0, Integer.min(this.row + row, 7));
            int newCol = Integer.max(0, Integer.min(this.col + col, 7));
            return new ScreenPosition(newRow, newCol);
        }
    }

    @Override
    public void onKeyStroke(KeyStroke keyStroke) {
        switch (cursorMode) {
            case START -> {
                switch (keyStroke.getKeyType()) {
                    case ArrowLeft -> startPositionCursor = startPositionCursor.withRelative(0, -1);
                    case ArrowRight -> startPositionCursor = startPositionCursor.withRelative(0, 1);
                    case ArrowDown -> startPositionCursor = startPositionCursor.withRelative(1, 0);
                    case ArrowUp -> startPositionCursor = startPositionCursor.withRelative(-1, 0);
                    case Enter -> selectStartPiece();
                }
            }
        }
    }

    @Override
    public void draw() {
        textGraphics.setBackgroundColor(ORANGE);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, textGraphics.getSize(), ' ');

        TerminalPosition boardStartPosition = TerminalPosition.OFFSET_1x1;

        TerminalPosition notificationsStartPosition = boardStartPosition.withRelativeColumn(BOARD_WIDTH + 2);
        drawNotifications(notificationsStartPosition);

        TerminalPosition pieceCursorStartPosition = boardStartPosition.withRelative(BOARD_WIDTH + 2, EDGE_THICK_H + 5 * 2);
        drawCursorInfo(pieceCursorStartPosition);

        TerminalPosition helpStartPosition = boardStartPosition.withRelative(BOARD_WIDTH + 2, EDGE_THICK_H + 5 * SQUARE_ROWS);
        if (showHelp) {
            drawHelp(helpStartPosition);
        }

        drawBoardEdge(boardStartPosition);
        drawBoard(boardStartPosition.withRelative(EDGE_THICK_V, EDGE_THICK_H));
    }

    private void drawCursorInfo(TerminalPosition pieceCursorStartPosition) {
        String cursorString = "";
        String instructionString = "";

        switch (cursorMode) {
            case START -> {
                assert startPositionCursor != null;
                cursorString = "[%s]".formatted(startPositionCursor.toAlgNot(myTeam));
                instructionString = "ENTER = Select piece, F3 = Cancel";
            }
            case END -> {
                assert startPositionCursor != null && endPositionCursor != null;
                cursorString = " %s  -> [%s]".formatted(startPositionCursor.toAlgNot(myTeam), endPositionCursor.toAlgNot(myTeam));
                instructionString = "F1 = Select new piece, F2 = Submit, F3 = Cancel";
            }
        }

        textGraphics.putString(pieceCursorStartPosition, cursorString);
        textGraphics.putString(pieceCursorStartPosition.withRelativeRow(1), instructionString);
    }

    private void drawNotifications(TerminalPosition notificationsStartPosition) {
        textGraphics.putString(notificationsStartPosition, "Messages", SGR.BOLD);
        for (int i = 0; i < 5 && i < notifications.size(); i++) {
            PresentableMessage notification = notifications.get(i);
            if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                textGraphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            } else {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            }
            textGraphics.putString(
                    notificationsStartPosition.withRelative(1, i + 1),
                    notification.getMessage());
        }
    }

    private void drawHelp(TerminalPosition startPosition) {
        textGraphics.putString(startPosition, "Help");
        for (int i = 0; i < helpStrings.length; i++) {
            textGraphics.setModifiers(EnumSet.of(SGR.BOLD));
            textGraphics.putString(startPosition.withRelativeRow(i + 1), fnKeys[i]);

            textGraphics.clearModifiers();
            textGraphics.putString(startPosition.withRelative(fnKeys[i].length() + 1, i + 1), helpStrings[i]);
        }
    }

    private void drawBoard(TerminalPosition startPosition) {
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
        textGraphics.fillRectangle(startPosition, horizontalEdge, ' ');
        TerminalPosition bottomEdge = startPosition.withRelativeRow(EDGE_THICK_H + SQUARE_ROWS * 8);
        textGraphics.fillRectangle(bottomEdge, horizontalEdge, ' ');

        TerminalSize verticalEdge = new TerminalSize(EDGE_THICK_V, SQUARE_ROWS * 8 );
        TerminalPosition leftEdge = startPosition.withRelativeRow(EDGE_THICK_H);
        textGraphics.fillRectangle(leftEdge, verticalEdge, ' ');
        TerminalPosition rightEdge = startPosition.withRelative(EDGE_THICK_V + SQUARE_COLS * 8, EDGE_THICK_H);
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
            TerminalPosition topLetterPosition = startPosition.withRelativeColumn(EDGE_THICK_V + 2 + SQUARE_COLS * i);
            textGraphics.putString(topLetterPosition, "%c".formatted(LETTERS[chessPosition.getColumn() - 1]));
            TerminalPosition bottomLetterPosition = topLetterPosition.withRelativeRow(EDGE_THICK_H + 8 * SQUARE_ROWS);
            textGraphics.putString(bottomLetterPosition, "%c".formatted(LETTERS[chessPosition.getColumn() - 1]));

            TerminalPosition leftNumberPosition = startPosition.withRelative(1, EDGE_THICK_H + 1 + SQUARE_ROWS * i);
            textGraphics.putString(leftNumberPosition, "%d".formatted(chessPosition.getRow()));
            TerminalPosition rightNumberPosition = leftNumberPosition.withRelativeColumn(EDGE_THICK_V - 1 + SQUARE_COLS * 8);
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

    void showLegalMoves() {
        cursorMode = CursorMode.START;
        startPositionCursor = new ScreenPosition(7, 0);
        endPositionCursor = null;
    }

    void selectStartPiece() {
        cursorMode = CursorMode.END;
        endPositionCursor = new ScreenPosition(0, 7);
    }

    void toggleHelpScreen() {
        showHelp = !showHelp;
    }

    void updateGame(GameData gameData) {
        chessGame = gameData.game();
    }

    protected void onSubmitMove() { }
    protected void onReload() {
        cursorMode = CursorMode.DISABLED;
        startPositionCursor = null;
        endPositionCursor = null;
    }
    protected void onResign() { }
    protected void onLeave() {
        unwind.run();
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION, ERROR -> notifications.addFirst((PresentableMessage) message);
            case LOAD_GAME -> updateGame(((LoadGameMessage) message).getGame());
        }
    }
}
