package ui.views;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.Drawable;

public abstract class View extends Drawable {

    public View(TextGraphics parentTextGraphics) {
        super(parentTextGraphics);
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
}
