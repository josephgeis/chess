package ui.views;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.menubar.MenuBarItem;
import ui.menubar.MenuItems;

import static ui.EventPublisher.EventType.*;

public class ListGamesView extends View {

    Runnable unwind;

    public ListGamesView(TextGraphics parentTextGraphics, Runnable unwind) {
        super(parentTextGraphics);
        this.unwind = unwind;

        menuItems = new MenuItems() {
            @Override
            protected MenuBarItem itemAt(int i) {
                return switch (i) {
                    case 2 -> MenuBarItem.withEvent("JoinGame", JOIN_GAME);
                    case 3 -> MenuBarItem.withEvent("Refresh", LIST_GAME);
                    case 4 -> MenuBarItem.withEvent("SpecGame", SPECTATE_GAME);
                    case 6 -> MenuBarItem.withEvent("Done", CANCEL);
                    default -> null;
                };
            }
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
        registerEventHandler(CANCEL, unwind);
    }

    @Override
    public void draw() {
        textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.fillRectangle(
                TerminalPosition.TOP_LEFT_CORNER,
                textGraphics.getSize(),
                ' '
        );

        textGraphics.putString(TerminalPosition.OFFSET_1x1, "240 Chess Client");
    }
}
