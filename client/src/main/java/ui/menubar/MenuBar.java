package ui.menubar;

import client.ClientState;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyType;
import ui.EventPublisher;

public class MenuBar extends ui.Drawable {
    MenuItems menuItems = MenuItems.NONE;

    public MenuBar(TextGraphics parentTextGraphics) {
        super(parentTextGraphics);
    }

    @Override
    public void setTextGraphics(TextGraphics parentTextGraphics) {
        super.setTextGraphics(parentTextGraphics.newTextGraphics(
                MenuBar.getBottomRowStart(parentTextGraphics.getSize()),
                MenuBar.getMenuBarSize(parentTextGraphics.getSize())));
    }

    public static TerminalPosition getBottomRowStart(TerminalSize terminalSize) {
        return new TerminalPosition(0, terminalSize.getRows() - 1);
    }

    public static TerminalSize getMenuBarSize(TerminalSize terminalSize) {
        return new TerminalSize(terminalSize.getColumns(), 1);
    }

    public void setMenuItems(MenuItems menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public void draw() {

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

    public EventPublisher.EventType getEventForMenuKey(KeyType keyType) {
        int itemNumber = switch (keyType) {
            case F1 -> 1;
            case F2 -> 2;
            case F3 -> 3;
            case F4 -> 4;
            case F5 -> 5;
            case F6 -> 6;
            default -> 0;
        };

        MenuBarItem menuBarItem = menuItems.itemAt(itemNumber);
        if (menuBarItem != null) {
            return menuBarItem.eventType();
        } else {
            return null;
        }
    }
}
