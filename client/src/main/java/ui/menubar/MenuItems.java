package ui.menubar;

public abstract class MenuItems {
    public static MenuItems NOT_LOGGED_IN = new MenuItems() {
        @Override
        protected MenuBarItem itemAt(int i) {
            return switch (i) {
                case 1 -> new MenuBarItem("Login");
                case 2 -> new MenuBarItem("Register");
                case 5 -> new MenuBarItem("Help");
                case 6 -> new MenuBarItem("Quit");
                default -> null;
            };
        }
    };

    public static MenuItems LOGGED_IN = new MenuItems() {
        @Override
        protected MenuBarItem itemAt(int i) {
            return switch(i) {
                case 1 -> new MenuBarItem("New Game");
                case 2 -> new MenuBarItem("JoinGame");
                case 3 -> new MenuBarItem("ListGame");
                case 4 -> new MenuBarItem("SpecGame");
                case 5 -> new MenuBarItem("Help");
                case 6 -> new MenuBarItem("Log Out");
                default -> null;
            };
        }
    };

    final public MenuBarItem getItem(int i) {
        if (i < 1 || i > 10) {
            throw new IllegalStateException("Unexpected value: " + i);
        }

        return itemAt(i);
    }

    protected MenuBarItem itemAt(int i) {
        return null;
    }
}
