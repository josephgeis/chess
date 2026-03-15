package ui.menubar;

import ui.EventPublisher;

public record MenuBarItem(String name, EventPublisher.EventType eventType) {
    public String menuTitle() {
        return name.substring(0, Integer.min(8, name.length()));
    }
}
