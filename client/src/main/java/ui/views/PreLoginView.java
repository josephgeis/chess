package ui.views;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.EventPublisher;
import ui.menubar.MenuBarItem;
import ui.menubar.MenuItems;

import java.util.EnumSet;

import static ui.EventPublisher.EventType.*;
import static ui.EventPublisher.EventType.QUIT_PROGRAM;

public abstract class PreLoginView extends MainMenuView {
    public PreLoginView(TextGraphics parentTextGraphics) {
        super(parentTextGraphics);

        menuItems = new MenuItems() {
            @Override
            protected MenuBarItem itemAt(int i) {
                return switch (i) {
                    case 1 -> MenuBarItem.withCallback("Login", PreLoginView.this::showLoginModal);
                    case 2 -> MenuBarItem.withCallback("Register", PreLoginView.this::showRegisterModal);
                    case 5 -> MenuBarItem.withCallback("Help", PreLoginView.this::toggleHelpScreen);
                    case 6 -> MenuBarItem.withEvent("Quit", QUIT_PROGRAM);
                    default -> null;
                };
            }
        };

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
        registerEventHandler(EventPublisher.EventType.REGISTER, this::showRegisterModal);
    }

    public abstract void showLoginModal();
    public abstract void showRegisterModal();
}
