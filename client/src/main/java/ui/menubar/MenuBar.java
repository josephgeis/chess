package ui.menubar;

import client.ClientState;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class MenuBar extends ui.Drawable {
    TextGraphics textGraphics;
    MenuItems menuItems;

    public MenuBar(TextGraphics parentTextGraphics) {
        super(parentTextGraphics);
    }

    @Override
    public void setTextGraphics(TextGraphics parentTextGraphics) {
        this.textGraphics = parentTextGraphics.newTextGraphics(
                MenuBar.getBottomRowStart(parentTextGraphics.getSize()),
                MenuBar.getMenuBarSize(parentTextGraphics.getSize()));
    }

    public static TerminalPosition getBottomRowStart(TerminalSize terminalSize) {
        return new TerminalPosition(0, terminalSize.getRows() - 1);
    }

    public static TerminalSize getMenuBarSize(TerminalSize terminalSize) {
        return new TerminalSize(terminalSize.getColumns(), 1);
    }

    @Override
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
