package ui.views;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.Drawable;
import ui.EventObserver;
import ui.TerminalController;

public abstract class View extends Drawable implements EventObserver {

    protected TerminalController terminalController;

    public View(TextGraphics parentTextGraphics, TerminalController terminalController) {
        super(parentTextGraphics);
        this.terminalController = terminalController;
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
}
