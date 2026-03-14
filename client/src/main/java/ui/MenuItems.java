package ui;

public abstract class MenuItems {
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
