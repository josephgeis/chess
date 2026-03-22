package ui.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.EventPublisher;
import ui.menubar.MenuBarItem;
import ui.menubar.MenuItems;

import static ui.EventPublisher.EventType.LOG_OUT;

public abstract class LoggedInView extends MainMenuView {
    public LoggedInView(TextGraphics parentTextGraphics, String username) {
        super(parentTextGraphics);

        menuItems = new MenuItems() {
            @Override
            protected MenuBarItem itemAt(int i) {
                return switch(i) {
                    case 1 -> MenuBarItem.withCallback("New Game", LoggedInView.this::onCreateGame);
                    case 2 -> MenuBarItem.noop("JoinGame");
                    case 3 -> MenuBarItem.withCallback("ListGame", LoggedInView.this::onListGames);
                    case 4 -> MenuBarItem.noop("SpecGame");
                    case 5 -> MenuBarItem.withCallback("Help", LoggedInView.this::toggleHelpScreen);
                    case 6 -> MenuBarItem.withCallback("Log Out", LoggedInView.this::onLogout);
                    default -> null;
                };
            }
        };

        fnKeys = new String[]{"F1", "F2", "F3", "F4", "F5", "F6"};
        helpStrings = new String[]{
                "Create a new game",
                "Join an existing game",
                "List existing games",
                "Observe an existing game",
                "Toggle this help message",
                "Log out from the client"
        };
        tagline = "Logged in as " + username;

        backgroundColor = TextColor.ANSI.GREEN;
        foregroundColor = TextColor.ANSI.WHITE_BRIGHT;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        registerEventHandler(EventPublisher.EventType.LIST_GAME, this::onListGames);
        registerEventHandler(EventPublisher.EventType.LOG_OUT, this::onLogout);
    }

    protected abstract void onCreateGame();
    protected abstract void onListGames();
    protected abstract void onLogout();
}
