package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

public abstract class Drawable {
    protected TextGraphics textGraphics;

    public Drawable(TextGraphics parentTextGraphics) {
        setTextGraphics(parentTextGraphics);
    }

    public void setTextGraphics(TextGraphics parentTextGraphics) {
        TerminalSize terminalSize = parentTextGraphics.getSize();

        this.textGraphics = parentTextGraphics.newTextGraphics(
                TerminalPosition.TOP_LEFT_CORNER,
                terminalSize.withRelativeRows(-1));
    }

    abstract public void draw();
}
