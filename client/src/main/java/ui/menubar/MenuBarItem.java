package ui.menubar;

public record MenuBarItem(String name, Runnable callback) {
    public String menuTitle() {
        return name.substring(0, Integer.min(8, name.length()));
    }

    public static MenuBarItem withCallback(String name, Runnable callback) {
        return new MenuBarItem(name, callback);
    }
}
