package ui.views;

import chess.*;
import client.MessageObserver;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import model.GameData;
import ui.menubar.MenuBarItem;
import ui.menubar.MenuItems;
import websocket.messages.LoadGameMessage;
import websocket.messages.PresentableMessage;
import websocket.messages.ServerMessage;

import java.util.*;
import java.util.stream.Collectors;

public class ChessBoardView extends View implements MessageObserver {

    private static final TextColor NAVY = new TextColor.RGB(0, 46, 93);
    private static final TextColor ROYAL = new TextColor.RGB(0, 61, 165);
    private static final TextColor ORANGE = new TextColor.RGB(209, 65, 36);
    private static final TextColor EDGE_COLOR = new TextColor.RGB(209, 204, 189);
    private static final TextColor LABEL_COLOR = new TextColor.RGB(163, 147, 130);
    private static final Character[] LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    final int SQUARE_COLS = 5;
    final int SQUARE_ROWS = 3;
    final int EDGE_THICK_H = 1;
    final int EDGE_THICK_V = 2;

    final int BOARD_WIDTH = (EDGE_THICK_V * 2) + 8 * SQUARE_COLS;

    boolean showHelp = false;
    String[] fnKeys = {"F1", "F2", "F3", "F4", "F5", "F6"};
    String[] helpStrings = {
            "Show legal moves for a piece",
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

    String moveInputString = "";
    ScreenPosition startPositionCursor;
    Set<ScreenPosition> validMoveLocations = new HashSet<>();

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
                    case 1 -> MenuBarItem.withCallback("ShowMove", ChessBoardView.this::showLegalMoves);
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

        public static ScreenPosition fromChessPosition(ChessPosition chessPosition, ChessGame.TeamColor perspective) {
            int row = chessPosition.getRow();
            int col = chessPosition.getColumn();

            if (perspective == ChessGame.TeamColor.BLACK) {
                return new ScreenPosition(row - 1, 8 - col);
            } else {
                return new ScreenPosition(8 - row, col - 1);
            }
        }

        public String toAlgNot(ChessGame.TeamColor perspective) {
            ChessPosition chessPosition = this.toChessPosition(perspective);
            Character column = (char) ('a' + chessPosition.getColumn() - 1);
            return "%c%d".formatted(column, chessPosition.getRow());
        }

        public static ScreenPosition fromAlgNot(String algNot, ChessGame.TeamColor perspective) {
            assert algNot.length() == 2;
            int col = algNot.charAt(0) - 'a';
            assert 0 <= col && col <= 7;
            int row = algNot.charAt(1) - '1';
            assert 0 <= row && row <= 7;

            if (perspective == ChessGame.TeamColor.BLACK) {
                return new ScreenPosition(row, 7 - col);
            } else {
                return new ScreenPosition(7 - row, col);
            }
        }

        public ScreenPosition withRelative(int row, int col) {
            int newRow = Integer.max(0, Integer.min(this.row + row, 7));
            int newCol = Integer.max(0, Integer.min(this.col + col, 7));
            return new ScreenPosition(newRow, newCol);
        }
    }

    void updateMoveInputString(KeyStroke keyStroke) {
        assert keyStroke.getKeyType() == KeyType.Backspace || keyStroke.getKeyType() == KeyType.Character;

        if (keyStroke.getKeyType() == KeyType.Backspace && !moveInputString.isEmpty()) {
            moveInputString = moveInputString.substring(0, moveInputString.length() - 1);
        } else {
            Character addCharacter = keyStroke.getCharacter();
            boolean validCharacter = switch (moveInputString.length()) {
                case 0, 2 -> 'a' <= addCharacter && addCharacter <= 'h';
                case 1, 3 -> '1' <= addCharacter && addCharacter <= '8';
                case 4 -> "RNBQ".indexOf(addCharacter) > -1;
                default -> false;
            };
            if (validCharacter) {
                moveInputString = moveInputString + addCharacter;
            }
        }
    }

    @Override
    public void onKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Backspace || keyStroke.getKeyType() == KeyType.Character) {
            updateMoveInputString(keyStroke);
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
        assert this.moveInputString.length() <= 5;
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        String moveInputString = "%-5s".formatted(this.moveInputString);
        String start = moveInputString.substring(0, 2);
        String end = moveInputString.substring(2, 4);
        String promotion = moveInputString.substring(4, 5);

        textGraphics.putString(pieceCursorStartPosition.withRelativeRow(0), "Start: [%2s]".formatted(start));
        textGraphics.putString(pieceCursorStartPosition.withRelativeRow(1), "  End: [%2s]".formatted(end));
        textGraphics.putString(pieceCursorStartPosition.withRelativeRow(2), "Promo: [%1s]".formatted(promotion));
        textGraphics.putString(pieceCursorStartPosition.withRelativeRow(3), getGameStatusString(), SGR.BOLD);
    }

    private String getGameStatusString() {
        ChessGame.TeamColor team = chessGame.getResignedTeam();
        String status;

        if (team != null) {
            status = "%s resigned";
        } else {
            team = chessGame.getTeamTurn();
            if (chessGame.isInCheckmate(team)) {
                status = "Checkmate: %s";
            } else if (chessGame.isInCheck(team)) {
                status = "Check: %s";
            } else if (chessGame.isInStalemate(team)) {
                status = "Stalemate: %s";
            } else {
                status = "It is %s's turn";
            }
        }

        return status.formatted(team.toString());
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
                drawSquare(startPosition, screenPosition);
            }
        }
    }

    private void drawBoardEdge(TerminalPosition startPosition) {
        textGraphics.setBackgroundColor(EDGE_COLOR);

        TerminalSize horizontalEdge = new TerminalSize(SQUARE_COLS * 8 + 2 * EDGE_THICK_V, 1);
        textGraphics.fillRectangle(startPosition, horizontalEdge, ' ');
        TerminalPosition bottomEdge = startPosition.withRelativeRow(EDGE_THICK_H + SQUARE_ROWS * 8);
        textGraphics.fillRectangle(bottomEdge, horizontalEdge, ' ');

        TerminalSize verticalEdge = new TerminalSize(EDGE_THICK_V, SQUARE_ROWS * 8);
        TerminalPosition leftEdge = startPosition.withRelativeRow(EDGE_THICK_H);
        textGraphics.fillRectangle(leftEdge, verticalEdge, ' ');
        TerminalPosition rightEdge = startPosition.withRelative(EDGE_THICK_V + SQUARE_COLS * 8, EDGE_THICK_H);
        textGraphics.fillRectangle(rightEdge, verticalEdge, ' ');

        drawEdgeAlphaNumber(startPosition);
    }

    private void drawEdgeAlphaNumber(TerminalPosition startPosition) {
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

    void drawSquare(TerminalPosition terminalPosition, ScreenPosition screenPosition) {
        int i = screenPosition.row();
        int j = screenPosition.col();

        ChessGame.TeamColor teamColor = (i + j) % 2 == 0 ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        ChessPiece piece = chessGame.getBoard().getPiece(screenPosition.toChessPosition(myTeam));

        TextColor squareColor = getSquareColor(screenPosition, teamColor);

        TerminalPosition position = terminalPosition.withRelative(5 * j, 3 * i);
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

    private TextColor getSquareColor(ScreenPosition screenPosition, ChessGame.TeamColor teamColor) {
        TextColor squareColor;

        if (screenPosition.equals(startPositionCursor)) {
            squareColor = switch (teamColor) {
                case WHITE -> TextColor.ANSI.GREEN_BRIGHT;
                case BLACK -> TextColor.ANSI.GREEN;
            };
        } else if (validMoveLocations != null && validMoveLocations.contains(screenPosition)) {
            squareColor = switch (teamColor) {
                case WHITE -> TextColor.ANSI.YELLOW_BRIGHT;
                case BLACK -> TextColor.ANSI.YELLOW;
            };
        } else {
            squareColor = switch (teamColor) {
                case WHITE -> ROYAL;
                case BLACK -> NAVY;
            };
        }

        return squareColor;
    }

    TextColor colorFor(ChessGame.TeamColor teamColor) {
        return switch (teamColor) {
            case WHITE -> TextColor.ANSI.WHITE_BRIGHT;
            case BLACK -> TextColor.ANSI.BLACK;
        };
    }

    void showLegalMoves() {
        if (moveInputString.length() < 2) {
            return;
        }

        String start = moveInputString.substring(0, 2);
        startPositionCursor = ScreenPosition.fromAlgNot(start, myTeam);

        Collection<ChessMove> validMoves = chessGame.validMoves(startPositionCursor.toChessPosition(myTeam));
        if (validMoves == null) {
            validMoveLocations = null;
        } else {
            validMoveLocations =
                    validMoves.stream().map(move ->
                                    ScreenPosition.fromChessPosition(move.getEndPosition(), myTeam))
                            .collect(Collectors.toSet());
        }
    }

    void toggleHelpScreen() {
        showHelp = !showHelp;
    }

    void updateGame(GameData gameData) {
        chessGame = gameData.game();
    }

    protected void onSubmitMove() {
        assert this.moveInputString.length() <= 5;
        if (this.moveInputString.length() < 4) {
            return;
        }
        String moveInputString = "%-5s".formatted(this.moveInputString);
        String start = moveInputString.substring(0, 2);
        String end = moveInputString.substring(2, 4);
        String promotion = "";
        if (this.moveInputString.length() == 5) {
            promotion = moveInputString.substring(4, 5);
        }

        ChessPosition startPosition = ScreenPosition.fromAlgNot(start, myTeam).toChessPosition(myTeam);
        ChessPosition endPosition = ScreenPosition.fromAlgNot(end, myTeam).toChessPosition(myTeam);
        ChessPiece.PieceType promotionType = ChessPiece.PieceType.fromInitial(promotion);
        ChessMove move = new ChessMove(startPosition, endPosition, promotionType);

        submitMove(move);
    }

    protected void submitMove(ChessMove move) {
    }

    protected void onReload() {
        moveInputString = "";
        startPositionCursor = null;
        validMoveLocations = null;
    }

    protected void onResign() {
    }

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
