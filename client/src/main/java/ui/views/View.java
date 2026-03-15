package ui.views;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import ui.Drawable;
import ui.EventObserver;
import ui.TerminalController;
import ui.menubar.MenuItems;

public abstract class View extends Drawable implements EventObserver {

    protected TerminalController terminalController;
    protected MenuItems menuItems = MenuItems.NONE;

    public View(TextGraphics parentTextGraphics, TerminalController terminalController) {
        super(parentTextGraphics);
        this.terminalController = terminalController;

    }

    public MenuItems getMenuItems() {
        return menuItems;
    }

    @Override
    public void setTextGraphics(TextGraphics parentTextGraphics) {
        TerminalSize terminalSize = parentTextGraphics.getSize();

        super.setTextGraphics(parentTextGraphics.newTextGraphics(
                TerminalPosition.TOP_LEFT_CORNER,
                terminalSize.withRelativeRows(-1)));
    }

    @Override
    abstract public void draw();

    public void onLoad() { }
    public void onUnload() {
        cancelEventHandlers();
    }

    public void onKeyStroke(KeyStroke keyStroke) {
        System.out.println("Key stroke handled by " +
                this.getClass() +
                ": " +
                keyStroke);
    }
}
