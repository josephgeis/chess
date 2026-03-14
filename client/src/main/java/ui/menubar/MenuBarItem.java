package ui.menubar;

public record MenuBarItem(String name) {
    public String menuTitle() {
        return name.substring(0, Integer.min(8, name.length()));
    }
}
