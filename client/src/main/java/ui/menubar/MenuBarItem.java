package ui.menubar;

import ui.EventPublisher;

public record MenuBarItem(String name, Runnable callback, EventPublisher.EventType eventType) {
    public String menuTitle() {
        return name.substring(0, Integer.min(8, name.length()));
    }

    public static MenuBarItem withEvent(String name, EventPublisher.EventType eventType) {
        return new MenuBarItem(name, null, eventType);
    }

    public static MenuBarItem withCallback(String name, Runnable callback) {
        return new MenuBarItem(name, callback, null);
    }

    public static MenuBarItem noop(String name) {
        return new MenuBarItem(name, null, null);
    }
}
