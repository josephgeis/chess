package ui.menubar;

import static ui.EventPublisher.EventType.*;

public abstract class MenuItems {
    public static final MenuItems NONE = new MenuItems() { };

    protected MenuBarItem itemAt(int i) {
        return null;
    }
}
