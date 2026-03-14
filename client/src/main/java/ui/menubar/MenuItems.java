package ui.menubar;

import static ui.TerminalController.EventType.*;

public abstract class MenuItems {
    public static MenuItems NOT_LOGGED_IN = new MenuItems() {
        @Override
        protected MenuBarItem itemAt(int i) {
            return switch (i) {
                case 1 -> new MenuBarItem("Login", LOG_IN);
                case 2 -> new MenuBarItem("Register", REGISTER);
                case 5 -> new MenuBarItem("Help", null);
                case 6 -> new MenuBarItem("Quit", QUIT_PROGRAM);
                default -> null;
            };
        }
    };

    public static MenuItems LOGGED_IN = new MenuItems() {
        @Override
        protected MenuBarItem itemAt(int i) {
            return switch(i) {
                case 1 -> new MenuBarItem("New Game", NEW_GAME);
                case 2 -> new MenuBarItem("JoinGame", JOIN_GAME);
                case 3 -> new MenuBarItem("ListGame", LIST_GAME);
                case 4 -> new MenuBarItem("SpecGame", SPECTATE_GAME);
                case 5 -> new MenuBarItem("Help", null);
                case 6 -> new MenuBarItem("Log Out", LOG_OUT);
                default -> null;
            };
        }
    };

    protected MenuBarItem itemAt(int i) {
        return null;
    }
}
