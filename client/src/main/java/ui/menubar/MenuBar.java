package ui.menubar;

import client.ClientState;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class MenuBar {
    TextGraphics textGraphics;
    MenuItems menuItems;

    public static MenuBar fromTextGraphics(TextGraphics fullTextGraphics) {
        MenuBar menuBar = new MenuBar();
        menuBar.setTextGraphics(fullTextGraphics);
        return menuBar;
    }

    public void setTextGraphics(TextGraphics fullTextGraphics) {
        this.textGraphics = fullTextGraphics.newTextGraphics(
                MenuBar.getBottomRowStart(fullTextGraphics.getSize()),
                MenuBar.getMenuBarSize(fullTextGraphics.getSize()));
    }

    public static TerminalPosition getBottomRowStart(TerminalSize terminalSize) {
        return new TerminalPosition(0, terminalSize.getRows() - 1);
    }

    public static TerminalSize getMenuBarSize(TerminalSize terminalSize) {
        return new TerminalSize(terminalSize.getColumns(), 1);
    }

    public void draw() {

        if (ClientState.isLoggedIn()) {
            menuItems = MenuItems.LOGGED_IN;
        } else {
            menuItems = MenuItems.NOT_LOGGED_IN;
        }

        textGraphics.setBackgroundColor(TextColor.ANSI.CYAN_BRIGHT);
        textGraphics.fillRectangle(new TerminalPosition(0, 0), textGraphics.getSize(), ' ');

        for (int i = 1; i <= 6; i++) {
            MenuBarItem item = menuItems.itemAt(i);

            String fnKey = "F%d".formatted(i);
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(8 * (i - 1), 0, fnKey);

            textGraphics.setBackgroundColor(TextColor.ANSI.CYAN_BRIGHT);
            textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
            if (item != null) {
                textGraphics.putString(8 * (i - 1) + 2, 0, item.menuTitle());
            }
        }
    }
}
