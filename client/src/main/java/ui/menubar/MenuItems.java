package ui.menubar;

import static ui.EventPublisher.EventType.*;

public abstract class MenuItems {
    public static final MenuItems NONE = new MenuItems() { };

    public static final MenuItems NOT_LOGGED_IN = new MenuItems() {
        @Override
        protected MenuBarItem itemAt(int i) {
            return switch (i) {
                case 1 -> MenuBarItem.withEvent("Login", LOG_IN);
                case 2 -> MenuBarItem.withEvent("Register", REGISTER);
                case 5 -> MenuBarItem.withEvent("Help", SHOW_HELP);
                case 6 -> MenuBarItem.withEvent("Quit", QUIT_PROGRAM);
                default -> null;
            };
        }
    };

    public static final MenuItems LOGGED_IN = new MenuItems() {
        @Override
        protected MenuBarItem itemAt(int i) {
            return switch(i) {
                case 1 -> MenuBarItem.withEvent("New Game", NEW_GAME);
                case 2 -> MenuBarItem.withEvent("JoinGame", JOIN_GAME);
                case 3 -> MenuBarItem.withEvent("ListGame", LIST_GAME);
                case 4 -> MenuBarItem.withEvent("SpecGame", SPECTATE_GAME);
                case 5 -> MenuBarItem.withEvent("Help", SHOW_HELP);
                case 6 -> MenuBarItem.withEvent("Log Out", LOG_OUT);
                default -> null;
            };
        }
    };

    protected MenuBarItem itemAt(int i) {
        return null;
    }
}
