package ui.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.EventPublisher;
import ui.menubar.MenuItems;

public class LoggedInView extends MainMenuView {
    String username;

    public LoggedInView(TextGraphics parentTextGraphics, String username) {
        super(parentTextGraphics);

        menuItems = MenuItems.LOGGED_IN;

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
    }
}
