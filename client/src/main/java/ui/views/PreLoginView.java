package ui.views;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.EventPublisher;
import ui.menubar.MenuItems;

import java.util.EnumSet;

public class PreLoginView extends MainMenuView {

    Runnable performLoginSegue;

    public PreLoginView(TextGraphics parentTextGraphics, Runnable performLoginSegue) {
        super(parentTextGraphics);
        this.performLoginSegue = performLoginSegue;

        menuItems = MenuItems.NOT_LOGGED_IN;

        fnKeys = new String[]{"F1", "F2", "F5", "F6"};
        helpStrings = new String[]{
                "Log in as an existing user",
                "Register as a new user",
                "Toggle this help message",
                "Quit the program"
        };
        tagline = "Not Logged In";

        backgroundColor = TextColor.ANSI.BLUE;
        foregroundColor = TextColor.ANSI.WHITE_BRIGHT;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        registerEventHandler(EventPublisher.EventType.LOG_IN, this::showLoginModal);
    }

    public void showLoginModal() {
        performLoginSegue.run();
    }
}
