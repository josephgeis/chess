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
    String idEntry = "";
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
        this.cursor = Integer.max(Integer.min(cursor, games.size() - 1), 0);
        idEntry = null;
    }

    void setIdEntry(String idEntry) {
        if (idEntry.isEmpty()) {
            this.idEntry = idEntry;
            return;
        }

        int cursor;
        try {
            cursor = Integer.parseInt(idEntry) - 1;
        } catch (NumberFormatException ignored) {
            assert false;
            return;
        }

        if (cursor >= games.size() || cursor < 0) {
            return;
        }
        this.idEntry = idEntry;
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
        if (games == null || games.isEmpty()) {
            return null;
        }

        assert cursor >= 0 && cursor < games.size();

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
            case ArrowDown -> setCursor(Integer.min(++cursor, games.size() - 1));
            case ArrowUp -> setCursor(Integer.max(--cursor, 0));
            case PageDown -> setCursor(Integer.min(cursor + gamesPerPage(), games.size() - 1));
            case PageUp -> setCursor(Integer.max(cursor - gamesPerPage(), 0));
            case Character -> {
                Character chr = keyStroke.getCharacter();
                if (chr >= '0' && chr <= '9') {
                    setIdEntry(idEntry != null ? idEntry + chr : chr.toString());
                }
            }
            case Backspace -> {
                if (idEntry != null && !idEntry.isEmpty()) {
                    setIdEntry(idEntry.substring(0, idEntry.length() - 1));
                }
            }
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
        TerminalPosition drawGamesPosition = TerminalPosition.OFFSET_1x1.withRelative(2, 2);
        if (games != null && !games.isEmpty()) {
            int idFieldDigits = (int) Math.floor(Math.log10(games.size() * 10));
            assert idFieldDigits > 0;
            textGraphics.putString(TerminalPosition.OFFSET_1x1.withRelativeColumn(41),
                    "Enter ID: [%%%ds]".formatted(idFieldDigits)
                            .formatted(idEntry != null ? idEntry : cursor + 1));
            drawGames(drawGamesPosition);
        } else {
            textGraphics.putString(drawGamesPosition, "No games available. Go back and create a game first.", SGR.ITALIC);
        }
    }

    protected void drawGames(TerminalPosition startPosition) {
        textGraphics.setModifiers(EnumSet.of(SGR.BOLD, SGR.UNDERLINE));
        drawGameListing(startPosition, "#","Name", "White Player", "Black Player");

        textGraphics.clearModifiers();
        boolean hasPreviousPage = skipGames() > 0;
        boolean hasNextPage = skipGames() + gamesPerPage() < games.size();
        if (gamesPerPage() == 1 && hasPreviousPage && hasNextPage) {
            textGraphics.putString(startPosition.withRelative(-1, 1), "⬍");
        } else {
            if (hasPreviousPage) {
                textGraphics.putString(startPosition.withRelative(-1, 1), "▲");
            }
            if (hasNextPage) {
                textGraphics.putString(startPosition.withRelative(-1, gamesPerPage()), "▼");
            }
        }

        for (int i = 0; i < gamesPerPage() &&  i + skipGames() < games.size(); i++) {
            TerminalPosition position = startPosition.withRelativeRow(1 + i);
            textGraphics.clearModifiers();

            if (cursor % gamesPerPage() == i) {
                textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
                textGraphics.fillRectangle(position, new TerminalSize(73, 1), ' ');
            } else {
                textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
            }

            GameListing game = games.get(i + skipGames());
            drawGameListing(position, "%d".formatted(i + skipGames() + 1),
                    game.gameName(),
                    game.whiteUsername(),
                    game.blackUsername());
        }
    }

    void drawGameListing(TerminalPosition position, String num, String gameName, String whitePlayer, String blackPlayer) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.putString(position, "%3s".formatted(num));

        position = position.withRelativeColumn(5);
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
