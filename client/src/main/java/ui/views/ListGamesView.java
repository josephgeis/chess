package ui.views;

import chess.ChessGame;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import response.GameListing;
import ui.menubar.MenuBarItem;
import ui.menubar.MenuItems;

import java.util.ArrayList;
import java.util.EnumSet;

public abstract class ListGamesView extends View {

    Runnable unwind;
    ArrayList<GameListing> games;
    int cursor = 0;
    final ViewMode viewMode;

    public ListGamesView(TextGraphics parentTextGraphics, ViewMode viewMode, Runnable unwind) {
        super(parentTextGraphics);
        this.unwind = unwind;
        this.viewMode = viewMode;

        menuItems = new MenuItems() {
            @Override
            protected MenuBarItem itemAt(int i) {
                return switch (i) {
                    case 1 -> viewMode == ViewMode.JOIN_GAME ? MenuBarItem.withCallback("JoinWht",
                            () -> onJoinGame(ChessGame.TeamColor.WHITE)) : null;
                    case 2 -> viewMode == ViewMode.JOIN_GAME ? MenuBarItem.withCallback("JoinBlk",
                            () -> onJoinGame(ChessGame.TeamColor.BLACK)) : null;
                    case 3 -> MenuBarItem.withCallback("Refresh", ListGamesView.this::reloadGames);
                    case 4 -> viewMode == ViewMode.SPECTATE_GAME ? MenuBarItem.withCallback("SpecGame", ListGamesView.this::onSpectateGame) : null;
                    case 6 -> MenuBarItem.withCallback("Back", unwind);
                    default -> null;
                };
            }
        };
    }

    public enum ViewMode {
        LIST_ONLY,
        JOIN_GAME,
        SPECTATE_GAME
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public void setGames(ArrayList<GameListing> games) {
        this.games = games;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        reloadGames();
    }

    String getTitle() {
        return switch (viewMode) {
            case LIST_ONLY -> "Currently active games";
            case JOIN_GAME -> "Choose a game to join";
            case SPECTATE_GAME -> "Choose a game to spectate";
        };
    }

    int gamesPerPage() {
        return Integer.max(textGraphics.getSize().getRows() - 5, 0);
    }

    protected GameListing getGameAtCursor() {
        if (cursor >= games.size()) {
            return null;
        }

        return games.get(cursor);
    }

    int getPage() {
        return cursor / gamesPerPage();
    }

    int skipGames() {
        return getPage() * gamesPerPage();
    }

    @Override
    public void onKeyStroke(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case ArrowDown -> cursor = Integer.min(++cursor, games.size() - 1);
            case ArrowUp -> cursor = Integer.max(--cursor, 0);
            case PageDown -> cursor = Integer.min(cursor + gamesPerPage(), games.size() - 1);
            case PageUp -> cursor = Integer.max(cursor - gamesPerPage(), 0);
        }
    }

    @Override
    public void draw() {
        textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.fillRectangle(
                TerminalPosition.TOP_LEFT_CORNER,
                textGraphics.getSize(),
                ' '
        );

        textGraphics.putString(TerminalPosition.OFFSET_1x1, getTitle());

        drawGames(TerminalPosition.OFFSET_1x1.withRelative(2, 2));
    }

    protected void drawGames(TerminalPosition startPosition) {
        textGraphics.setModifiers(EnumSet.of(SGR.BOLD, SGR.UNDERLINE));
        drawGameListing(startPosition, "Name", "White Player", "Black Player");

        for (int i = 0; i < gamesPerPage() &&  i + skipGames() < games.size(); i++) {
            TerminalPosition position = TerminalPosition.OFFSET_1x1.withRelative(2, 3 + i);
            textGraphics.clearModifiers();

            if (cursor % gamesPerPage() == i) {
                textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
                textGraphics.fillRectangle(position.withRelativeColumn(-1), new TerminalSize(70, 1), ' ');
            } else {
                textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
            }

            GameListing game = games.get(i + skipGames());
            drawGameListing(position,
                    game.gameName(),
                    game.whiteUsername(),
                    game.blackUsername());
        }
    }

    void drawGameListing(TerminalPosition position, String gameName, String whitePlayer, String blackPlayer) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.putString(position, "%-32s".formatted(gameName));

        position = position.withRelativeColumn(34);
        if (whitePlayer == null) {
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            textGraphics.putString(position, "-- OPEN --", SGR.ITALIC);
        } else {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            textGraphics.putString(position, "%-16s".formatted(whitePlayer));
        }

        position = position.withRelativeColumn(18);
        if (blackPlayer == null) {
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            textGraphics.putString(position, "-- OPEN --", SGR.ITALIC);
        } else {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            textGraphics.putString(position, "%-16s".formatted(blackPlayer));
        }
    }

    protected abstract void reloadGames();
    protected abstract void onJoinGame(ChessGame.TeamColor teamColor);
    protected abstract void onSpectateGame();
}
