package ui.menubar;

import ui.TerminalController;

public record MenuBarItem(String name, TerminalController.EventType eventType) {
    public String menuTitle() {
        return name.substring(0, Integer.min(8, name.length()));
    }
}
